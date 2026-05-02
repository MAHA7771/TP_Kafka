# TP Apache Kafka — Pipeline de logs en temps réel

## Prérequis
- Java 11+
- Apache Kafka installé et configuré
- Maven (optionnel, pour compilation)

## Démarrage de Kafka
1. Démarrer Zookeeper :
   ```
   bin\windows\zookeeper-server-start.bat config\zookeeper.properties
   ```
2. Démarrer Kafka :
   ```
   bin\windows\kafka-server-start.bat config\server.properties
   ```

## Création des topics
```
kafka-topics.bat --create --topic pos-events --bootstrap-server localhost:9092 --partitions 4 --replication-factor 1
kafka-topics.bat --create --topic alertes-retours --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

## Compilation et exécution
### Avec Maven
```
mvn compile
mvn exec:java -Dexec.mainClass="com.example.pos.SimulateurCaisse"
mvn exec:java -Dexec.mainClass="com.example.pos.ChiffreAffairesParVille"
mvn exec:java -Dexec.mainClass="com.example.pos.DetecteurAnomalies"
```

### Avec javac (manuel)
Ajouter les jars Kafka et Gson au classpath.

## Description
- **SimulateurCaisse** : Producteur simulant les événements POS
- **ChiffreAffairesParVille** : Consommateur calculant le CA par ville
- **DetecteurAnomalies** : Consommateur détectant les retours > 200 DT