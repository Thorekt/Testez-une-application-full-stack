# Yoga

## Back

Répertoire back

### Test

Lancer tout les tests (integtation + unitaire) et generer les rapports (all, unitaire et intégration):

> mvn clean verify

Lancer les tests unitaires uniquement et generer le rapport

> mvn clean test

Lancer les tests d'intégrations uniquement et generer le rapport

> mvn clean integration-test

Les rapports sont disponibles dans le répertoire target/site :

- jacoco-all -> rapport des tests combinés (integration + unitaire)
- jacoco-it -> rapport des tests d'intégrations
- jacoco-ut -> rapport des tests unitaires

### Installation

Build l'application :

> mvn clean install

Le fichier JAR ce trouve dans le repertoire target

### Lancer le projet

#### Mode développement

> mvn spring-boot:run

#### Lancement depuis fichier JAR

> java -jar target\yoga-app-0.0.1-SNAPSHOT.jar

## Front

Répertoire front

### Test

Exécuter tout les tests (unitaire + integration) et générer un rapport commun

> npm run test:jest:all

Exécuter tout les tests d'integrations et générer le rapport

> npm run test:jest:integration

Exécuter tout les tests unitaires et générer le rapport

> npm run test:jest:unit

Exécuter les 3 commandes précédentes

> npm run test:jest:reports

Exécuter les test cypress et générer le coverage

- Lancer le server en mode coverage :
  > npx ng run yoga:serve-coverage
- Dans un autre terminal, éxécuter les test en mode cli :
  > npx cypress run
- Génerer le coverage :
  > npm run e2e:coverage

Dans le répertoire coverage il y a les repertoire suivant :

- lcov-report -> contient le coverage e2e cypress
- jest/all -> contient le coverage unitaire + intégration
- jest/integration -> contient le coverage intégration
- jest/unitaire -> contient le coverage unitaire

### Instalation

Install dependencies:

> npm install

### Lancement

Launch Front-end:

> npm run start;

## Ressources

### Mockoon env

### Postman collection

For Postman import the collection

> ressources/postman/yoga.postman_collection.json

by following the documentation:

https://learning.postman.com/docs/getting-started/importing-and-exporting-data/#importing-data-into-postman

### MySQL

SQL script for creating the schema is available `ressources/sql/script.sql`

By default the admin account is:

- login: yoga@studio.com
- password: test!1234
