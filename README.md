# Welcome to use

Using PG to achieve real-time consumption of CDC incremental data can be used for:

- database cache dual write consistency

- real-time data change processing

- incremental data collection

- status control/event monitoring

# Quick start

> postgres cdc is a lightweight tool implemented in the Java language that can help you quickly build a simple incremental acquisition method. We use debezium to implement this process, and you can try to further process or upgrade your business according to your needs if needed

## Necessary configurations

1.Enable logical copy permission

```sql
-- Grant all permissions, including creating tables, inserting data, deleting data, modifying table structures, etc
GRANT ALL PRIVILEGES ON DATABASE postgres TO postgres;

-- Create a publication named dbz_ppublication_root and specify to publish data changes for all tables, while enabling the PULISH_VIA-PARTITION-ROOT option
CREATE PUBLICATION dbz_publication_root FOR ALL TABLES WITH (PUBLISH_VIA_PARTITION_ROOT = TRUE); 

-- Create a publication named dbz_publishation and specify to publish data changes for all tables
CREATE PUBLICATION dbz_publication FOR ALL TABLES;
```

2.Modify the **pg_hba.conf** configuration file, adding the following content to ensure CDC Instance can access the database

```text
# Replace username with the actual username
local   replication     username                     trust
host    replication     username  0.0.0.0/32         md5
host    replication     username  ::1/128            trust
```

3.Enable logical replication in ***postgresql.conf***, where the default Pgoutput plugin (which is supported on pg10+versions) is used. The following three parameters are represented in sequence: ***set the log level of WAL (Write Ahead Logging)***, **set the maximum allowed number of WAL sending processes**, ***and set the maximum allowed number of replication slots***

```text
wal_level = logical
max_wal_senders = 10
max_replication_slots = 10
```


## Developer's guide

1.Create your link parameter configuration

```java
final PGConfig pgConfig = PGConfig.config()
                .host("localhost")
                .port(5432)
                .database("postgres")
                .username("postgres")
                .password("123456")
                .offset("cache" + File.separator + "dbhistory.dat");
```

2.Build a binder to facilitate your registration of more event handlers based on this binder in the future

```java
final BaseReader.Binder binder = BaseReader.Binder
                .on("postgres", "public", "your_table_name");
```

3.Create a CDC consumption instance. If you need to create multiple instances, they are isolated from each other. Each instance implements the AutoCloseable interface, so you need to call the close() method to release resources after use

```java
final CdcInstance<PGConfig> cdcInstance = CdcFactory.createNew(Source.PG)
```

4.Use the configuration you created in the first step to initialize the CDC consumption instance

```java
cdcInstance.init(pgConfig);
```

5.Register the event handler you need for the corresponding binder, which is equivalent to registering callback events. You can use this to consume the corresponding CDC events

```java
//Registered insert event and printed the data content when adding data
cdcInstance.whenInsert(binder.asNewDefaultReader(event -> {
                final JsonNode after = event.getAfter();
                log.info("Event insert, after: {}", after.toString());
            }));

//Registered update event and printed data content when adding data
cdcInstance.whenUpdate(binder.asNewDefaultReader(event -> {
                final JsonNode before = event.getBefore();
                final JsonNode after = event.getAfter();
                log.info("Event update, before: {}, after: {}", before.toString(), after.toString());
            }));

//Registered delete event and printed data content when adding data
cdcInstance.whenDelete(binder.asNewDefaultReader(event -> {
                final JsonNode before = event.getBefore();
                log.info("Event delete, before: {}", before.toString());
            }))
```

6.Start launching your CDC consumption instance now

```java
cdcInstance.start();
```

7.Please release resources for CDC consumption instances before exiting the program/at necessary times

```java
cdcInstance.close();
```