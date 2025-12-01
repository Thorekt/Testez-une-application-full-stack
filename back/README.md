# Yoga App !

## Test

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

## Installation

Build l'application :

> mvn clean install

Le fichier JAR ce trouve dans le repertoire target

## Lancer le projet

### Mode développement

> mvn spring-boot:run

### Lancement depuis fichier JAR

> java -jar target\yoga-app-0.0.1-SNAPSHOT.jar
