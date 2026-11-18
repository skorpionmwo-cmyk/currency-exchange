# Сервис обмена валюты

Доработка [стартового проекта Skillbox](https://github.com/skillbox-java/p3_currency_exchange).
Приложение использует Java 17, Spring Boot, PostgreSQL и Liquibase. Курсы загружаются в XML
с помощью JAXB.

## Запуск

Подготовьте JDK 17 и PostgreSQL. Например, базу для локальной разработки можно запустить в Docker:

```bash
docker run -d --name currency-postgres -p 5432:5432 -e POSTGRES_PASSWORD=postgres postgres:16
./mvnw clean verify
./mvnw spring-boot:run
```

На Windows используйте `mvnw.cmd` вместо `./mvnw`. Если Maven уже установлен, подойдёт
`mvn clean verify`. На этапе `verify` выполняется Checkstyle.

### Windows и JDK 24

В PowerShell из папки проекта соберите приложение с версией Lombok, поддерживающей JDK 24:

```powershell
.\mvnw.cmd "-Dlombok.version=1.18.38" clean verify
```

При работающем PostgreSQL запустите собранный JAR. Если Java не может проверить
TLS-сертификат основного источника, задайте HTTPS-зеркало из задания Skillbox:

```powershell
$env:CBR_XML_URL = 'https://www.cbr-xml-daily.ru/daily_utf8.xml'
java -jar .\target\currency.exchange-0.0.1-SNAPSHOT-exec.jar
```

Значение `CBR_XML_URL` действует в текущем окне PowerShell. Если пароль локального
PostgreSQL отличается от значения по умолчанию, перед запуском задайте `DB_PASSWORD`.
Успешную загрузку подтверждает сообщение `CBR currency synchronization completed`.

### Настройки

| Переменная | Значение по умолчанию |
| --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/postgres` |
| `DB_USERNAME` | `postgres` |
| `DB_PASSWORD` | `postgres` |
| `CBR_XML_URL` | `https://cbr.ru/scripts/XML_daily.asp` |
| `CBR_SYNC_ENABLED` | `true` |

## API

```bash
curl http://localhost:8080/api/currency/
curl http://localhost:8080/api/currency/1333
curl 'http://localhost:8080/api/currency/convert?value=100&numCode=840'
```

Список возвращает объект `{"currencies":[{"name":"Доллар США","value":93.5224}]}`.
Число в примере взято из стартовой базы. Поле `value` содержит стоимость в рублях
за указанный в записи номинал. При конвертации сервис приводит курс к одной
единице валюты. Если в базе есть более свежая запись с буквенным кодом,
конвертация использует её.

По ID возвращается подробная запись с полем `isoCharCode`. У исходных записей оно
имеет значение `null`. Остаётся доступен и существующий `POST /api/currency/create`.

## Обновление валют

Миграция добавляет nullable-колонку `iso_char_code` с уникальным ограничением.
Исходные записи сохраняются. При запуске и затем каждый час сервис загружает
XML ЦБ, обновляет записи по буквенному коду и создаёт недостающие. Каждая загрузка
применяется одной транзакцией. В журнале есть результат и количество обработанных
валют. При ошибке загрузки прежние данные остаются доступны.
