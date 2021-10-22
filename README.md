# ISBN-Service
> A Spring Web Service for ISBN validation, used by another Spring Web Service storing books.

---
```
██ ███████ ██████  ███    ██       ███████ ███████ ██████  ██    ██ ██  ██████ ███████ 
██ ██      ██   ██ ████   ██       ██      ██      ██   ██ ██    ██ ██ ██      ██      
██ ███████ ██████  ██ ██  ██ █████ ███████ █████   ██████  ██    ██ ██ ██      █████   
██      ██ ██   ██ ██  ██ ██            ██ ██      ██   ██  ██  ██  ██ ██      ██      
██ ███████ ██████  ██   ████       ███████ ███████ ██   ██   ████   ██  ██████ ███████ 
```
---

## Getting started
There are several different ways to run the services.

### Using the deployed instance from Azure
The services are available under the following URLs:
- [saic-validation.azurewebsites.net:80/api.html](http://saic-validation.azurewebsites.net:80/api.html)
- [saic-isbn-backend.azurewebsites.net:80/api.html](http://saic-isbn-backend.azurewebsites.net:80/api.html)

**Warning**: Due to using the Azure Free Tier, the services aren't always running and may need some time to start.

### Using docker-compose
The services can also be started locally using docker-compose. For using the newest version from docker hub, execute the following:
```ps
docker-compose pull
docker-compose up
```
To build the containers from source instead, use the following commands:
```ps
docker-compose build --parallel
docker-compose up
```

### Running a single container
The containers can also be run seperately using docker. In order to start the backend service with the hosted service, execute the following commands:
```ps
docker pull capofcave/isbn-backend:latest
docker run -p "8080:8080" -i -e "server.port=8080" -e "validationService.url=saic-validation.azurewebsites.net:80" --name isbn-backend -t capofcave/isbn-backend
```
The containers can also be build from source by replacing the `docker pull` command with `docker build -t capofcave/isbn-backend ./SAIC-isbn-backend`.
