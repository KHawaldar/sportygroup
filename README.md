# Quick Start

**Prerequisites:**
- Docker installed and running
- Java 17 installed
- Gradle installed OR use the Gradle wrapper included in the project (`./gradlew`)

1. Clone the repository:

```bash
git clone git@github.com:KHawaldar/sportygroup.git
```
2. ```cd sportygroup```


3. Execute the following gradle command which creates the jar file in build directory.
 ```
./gradlew clean build
```
4. Make the start script executable (Linux/macOS):

```bash
chmod +x start.sh
```
5. Run the application for the desired environment:
```bash
./start.sh local
# or
./start.sh prod
# or
./start.sh qa
```
## Local Testing

After starting the application,  you can test the REST APIs via Swagger UI.

### Access Swagger UI

Open the following URL in your browser:

```http://<your-machine-ip>:8080/swagger-ui/index.html```

### How to find your machine IP address

Run the following command in your terminal:

```bash
hostname -I
```

Use that IP in the URL, for example:


http://172.19.66.192:8080/swagger-ui/index.html
to access Swagger UI and test the APIs.
