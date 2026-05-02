# TP Apache Kafka — Pipeline de logs en temps réel

## Prérequis
- Java 11+
- Apache Kafka (mode KRaft, sans Zookeeper)
- Maven 3+

---

## Démarrage de Kafka (mode KRaft)

### 1. Formater le stockage (une seule fois)
kafka-storage.bat format -t <CLUSTER_ID> -c config\kraft\server.properties

### 2. Démarrer Kafka broker
bin\windows\kafka-server-start.bat config\kraft\server.properties

> Le démarrage affiche un cluster-id et confirme le bon lancement du broker.

---

## Création des topics

kafka-topics.bat --create --topic pos-events --bootstrap-server localhost:9092 --partitions 4 --replication-factor 1

kafka-topics.bat --create --topic alertes-retours --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

---

## Compilation et exécution (Maven)

### Compilation
mvn clean compile

### Exécution

Simulateur (producer) :
mvn exec:java -Dexec.mainClass="com.example.pos.SimulateurCaisse"

Consommateur CA :
mvn exec:java -Dexec.mainClass="com.example.pos.ChiffreAffairesParVille"

Détecteur anomalies :
mvn exec:java -Dexec.mainClass="com.example.pos.DetecteurAnomalies"

---

## Description des composants

- **SimulateurCaisse** : produit des événements POS (VENTE, RETOUR, OUVERTURE)
- **ChiffreAffairesParVille** : calcule le chiffre d'affaires par ville en temps réel
- **DetecteurAnomalies** : détecte les RETOURS > 200 DT et envoie vers alertes-retours

---

## Étapes de test

### Étape 12
1 simulateur + 1 ChiffreAffaires + 1 DetecteurAnomalies

### Étape 13
2 simulateurs en parallèle

### Étape 14
3 instances de ChiffreAffaires (rebalance Kafka)

### Étape 15
Arrêt brutal d’un consumer → messages rejoués après rebalance

### Étape 16
Surveillance du lag :
kafka-consumer-groups.bat --describe --group ca-1 --bootstrap-server localhost:9092
