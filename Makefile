export LOAN_CALCULATOR_DB=loan_calculator
export LOAN_CALCULATOR_DB_PORT=3307
export LOAN_CALCULATOR_USERNAME=root
export LOAN_CALCULATOR_PASSWORD=test123

.PHONY: all
all: app_run

docker-compose:
	docker-compose up -d

wait_for_db: docker-compose
	@echo "Waiting for MySQL to be ready on container 'loan_calculator_mysql'..."
	@set -e; \
	ATTEMPTS=60; \
	until docker-compose exec -T mysql mysqladmin ping -h "127.0.0.1" -u"$(LOAN_CALCULATOR_USERNAME)" -p"$(LOAN_CALCULATOR_PASSWORD)" --silent; do \
	  ATTEMPTS=$$((ATTEMPTS - 1)); \
	  if [ $$ATTEMPTS -le 0 ]; then \
	    echo "MySQL did not become ready in time"; \
	    exit 1; \
	  fi; \
	  printf "."; \
	  sleep 2; \
	done; \
	echo " MySQL is ready."

app_run: wait_for_db
	mvn spring-boot:run

clean:
	@echo "Stopping Spring Boot application on port 8090..."
	@-kill -9 $$(lsof -ti:8090) 2>/dev/null || echo "No process found on port 8090"
	@echo "Stopping Docker containers..."
	docker-compose down

clean_volumes:
	@echo "Stopping Spring Boot application on port 8090..."
	@-kill -9 $$(lsof -ti:8090) 2>/dev/null || echo "No process found on port 8090"
	@echo "Stopping Docker containers and removing volumes..."
	docker-compose down -v

.PHONY: help
help:
	@echo "Available targets:"
	@echo "  make all (default)    - Start MySQL and run the Spring Boot application"
	@echo "  make docker-compose   - Start MySQL container only"
	@echo "  make wait_for_db      - Start MySQL and wait until it's ready"
	@echo "  make app_run          - Start MySQL, wait for it, and run the app"
	@echo "  make clean            - Stop and remove containers"
	@echo "  make clean_volumes    - Stop containers and remove volumes (deletes data)"

