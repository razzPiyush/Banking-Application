# Build stage
FROM maven:3.9.6-eclipse-temurin-17-jammy AS build

# Set working directory
WORKDIR /app

# Copy pom files first for better caching
COPY pom.xml .
COPY BankData/pom.xml BankData/
COPY BackOfficeSystem/pom.xml BackOfficeSystem/
COPY OnlineBanking/pom.xml OnlineBanking/
COPY TransactionScheduling/pom.xml TransactionScheduling/

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source files and build
COPY . .
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-focal

# Set working directory
WORKDIR /app

# Copy JARs from build stage
COPY --from=build /app/BackOfficeSystem/target/BackOfficeSystem-*.jar /app/backofficesystem-app.jar
COPY --from=build /app/OnlineBanking/target/OnlineBanking-*.jar /app/onlinebanking-app.jar
COPY --from=build /app/TransactionScheduling/target/TransactionScheduling-*.jar /app/transactionscheduling-app.jar

# Copy entrypoint script
COPY entrypoint.sh /app/
RUN chmod +x /app/entrypoint.sh

# Set environment variables
ENV JAVA_OPTS="-Xms512m -Xmx1024m" \
    BACKOFFICE_PORT=8080 \
    ONLINE_BANKING_PORT=8081 \
    TRANSACTION_PORT=8082

# Health check
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Expose ports
EXPOSE 8080 8081 8082

# Create and use non-root user
RUN addgroup --system javauser && adduser --system --ingroup javauser javauser
USER javauser

ENTRYPOINT ["/app/entrypoint.sh"]