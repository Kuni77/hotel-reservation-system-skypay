#  Hotel Reservation System - Skypay Technical Test

##  Description

Système de gestion de réservations d'hôtel développé en Java dans le cadre du test technique Skypay. Ce système permet de gérer des chambres, des utilisateurs et leurs réservations avec validation complète et gestion d'erreurs.

##  Fonctionnalités

-  Gestion des chambres (création, mise à jour)
-  Gestion des utilisateurs et de leurs soldes
-  Système de réservation avec validation :
  - Vérification du solde suffisant
  - Vérification de la disponibilité des chambres
  - Validation des dates
-  Conservation de l'historique des réservations (snapshot pattern)
-  Affichage trié des données (du plus récent au plus ancien)

##  Structure du Projet

```
hotel-reservation-system/
├── src/
│   ├── RoomType.java       # Énumération des types de chambres
│   ├── Room.java           # Entité chambre
│   ├── User.java           # Entité utilisateur
│   ├── Booking.java        # Entité réservation
│   ├── Service.java        # Service principal
│   └── Main.java           # Test case
├── design-questions.md     # Réponses aux questions de design
└── README.md
```

##  Compilation et Exécution

### Prérequis
- Java 8 ou supérieur
- Aucune dépendance externe requise

### Compilation
```bash
javac *.java
```

### Exécution
```bash
java Main
```

##  Test Case

Le test case inclus dans `Main.java` effectue les opérations suivantes :

1. **Création de 3 chambres** :
   - Room 1: STANDARD, 1000/nuit
   - Room 2: JUNIOR, 2000/nuit
   - Room 3: SUITE, 3000/nuit

2. **Création de 2 utilisateurs** :
   - User 1: Balance 5000
   - User 2: Balance 10000

3. **Tentatives de réservation** :
   - User 1 → Room 2 (30/06-07/07) :  Solde insuffisant
   - User 1 → Room 2 (07/07-30/06) :  Dates invalides
   - User 1 → Room 1 (07/07-08/07) :  Succès
   - User 2 → Room 1 (07/07-09/07) :  Conflit de dates
   - User 2 → Room 3 (07/07-08/07) :  Succès

4. **Mise à jour de Room 1** :
   - Changement en SUITE à 10000/nuit
   -  Les réservations existantes conservent les anciennes valeurs

##  Résultats Attendus

### Sortie `printAll()`
```
========== ALL ROOMS (Latest to Oldest) ==========
Room 1 | Type: SUITE | Price/Night: 10000
Room 3 | Type: SUITE | Price/Night: 3000
Room 2 | Type: JUNIOR | Price/Night: 2000

========== ALL BOOKINGS (Latest to Oldest) ==========
Booking ID: 2
  User ID: 2 (Balance at booking: 10000)
  Room: 3 | Type: SUITE | Price/Night: 3000
  Check-in: 07/07/2026
  Check-out: 08/07/2026
  Total Price: 3000

Booking ID: 1
  User ID: 1 (Balance at booking: 5000)
  Room: 1 | Type: STANDARD | Price/Night: 1000
  Check-in: 07/07/2026
  Check-out: 08/07/2026
  Total Price: 1000
```

### Sortie `printAllUsers()`
```
========== ALL USERS (Latest to Oldest) ==========
User ID: 2 | Balance: 7000
User ID: 1 | Balance: 4000
```

##  Points Clés de l'Implémentation

### 1. Snapshot Pattern
Les réservations stockent une copie des données de la chambre et de l'utilisateur au moment de la réservation :
```java
// Dans Booking.java
private RoomType roomTypeSnapshot;
private int roomPriceSnapshot;
private int userBalanceSnapshot;
```

**Avantages** :
- Les modifications futures des chambres n'affectent pas les réservations passées
- Historique complet et fidèle
- Facilite l'audit et la facturation

### 2. Validation Complète
```java
// Vérifications effectuées
✓ Dates valides (checkout > checkin)
✓ Solde suffisant
✓ Disponibilité de la chambre
✓ Existence de l'utilisateur et de la chambre
```

### 3. Gestion des Dates
- Normalisation des dates (année, mois, jour uniquement)
- Comparaison précise pour la disponibilité
- Format d'affichage cohérent (dd/MM/yyyy)

### 4. Gestion des Erreurs
```java
try {
    service.bookRoom(1, 2, checkIn, checkOut);
} catch (IllegalArgumentException e) {
    System.out.println("Error: " + e.getMessage());
}
```

##  Réponses aux Questions de Design

### Question 1: Service Unique
**Est-il recommandé de mettre toutes les fonctions dans un seul service ?**

**Non**, pour un système de production, il est préférable de séparer en plusieurs services :
- `RoomService` : Gestion des chambres
- `UserService` : Gestion des utilisateurs
- `BookingService` : Orchestration des réservations

**Avantages** :
- Meilleure testabilité
- Maintenance facilitée
- Respect du principe de responsabilité unique (SRP)
- Scalabilité améliorée

### Question 2: Impact de setRoom()
**Quelle est une alternative au snapshot pattern ?**

**Alternative** : Pattern de versioning
- Chaque modification crée une nouvelle version de la chambre
- Les réservations référencent la version spécifique

**Ma recommandation** : Approche hybride
- Snapshot pour les données contractuelles (prix)
- Référence pour les données opérationnelles
- Meilleur compromis entre simplicité et cohérence

Voir `design-questions.md` pour les détails complets.

##  Exigences Techniques Respectées

-  Réservation avec vérification du solde et disponibilité
-  `setRoom()` n'impacte pas les réservations existantes
-  `setRoom()` crée une chambre si elle n'existe pas
-  `setUser()` crée un utilisateur si il n'existe pas
-  `printAll()` affiche tout du plus récent au plus ancien
-  Affichage complet des données de réservation avec snapshot
-  `printAllUsers()` affiche les utilisateurs du plus récent au plus ancien
-  Pas de repositories, utilisation d'ArrayLists
-  Dates normalisées (année, mois, jour uniquement)
-  Gestion des exceptions

---

## 📧 Contact

Pour toute question : recrutement@skypay.ma
