# search-store

### Run Agent APM Server

````
java -javaagent:./elastic-apm-agent-1.32.0.jar \
-Delastic.apm.service_name=search-store  \
-Delastic.apm.server_urls=http://localhost:8200 \
-Delastic.apm.environment=dev \
-Delastic.apm.application_packages=com.company.searchstore \
-jar ./target/search-store-1.0.0.jar
````
