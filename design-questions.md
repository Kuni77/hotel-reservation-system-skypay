# Design Questions - Réponses

## Question 1: Approche Service Unique

### Question: Suppose we put all the functions inside the same service. Is this the recommended approach? Please explain.

### Réponse:

Non, ce n'est pas l'approche recommandée pour un système de production. Mettre toutes les fonctions dans un seul service viole plusieurs principes de conception logicielle :
Problèmes de cette approche :

- Violation du Principe de Responsabilité Unique (SRP)

    - Le Service gère à la fois les chambres, les utilisateurs ET les réservations
    - Il devient un "God Object" qui connaît et fait trop de choses 
    - Difficile à maintenir et à tester


- Faible Cohésion

    - Les opérations sur les chambres n'ont rien à voir avec les opérations utilisateurs
    - Le code devient difficile à comprendre et à naviguer


- Couplage Fort

    - Toute modification dans une partie du code peut affecter les autres parties
    - Impossible de réutiliser une partie de la logique indépendamment


- Difficultés de Test

    - Tests unitaires complexes car tout est interconnecté
    - Impossible de mocker facilement les dépendances

#### Approche Recommandée :
Séparer en plusieurs services spécialisés : RoomService, UserService et BookingService

- Avantages de la séparation :

    - Testabilité: Chaque service peut être testé indépendamment
    - Maintenabilité: Modifications isolées dans chaque domaine
    - Réutilisabilité: Services réutilisables dans d'autres contextes
    - Scalabilité: Possibilité de scaler chaque service indépendamment
    - Clarté: Code plus lisible et organisé


## Question 2: Approche setRoom() et Impact sur les Réservations

### Question: In this design, we chose to have a function setRoom(..) that should not impact the previous bookings. What is another way? What is your recommendation? Please explain and justify.

### Réponse:

#### Approche Actuelle (Snapshot Pattern)
Dans notre implémentation, nous utilisons le Snapshot Pattern :

- Les réservations stockent une copie des données de la chambre au moment de la réservation
- setRoom() peut modifier la chambre sans affecter les réservations passées
- Avantage : Historique préservé, audit trail clair
- Inconvénient : Duplication de données, incohérence si prix change

#### Alternative : Versioning Pattern
Une autre approche serait d'utiliser le versioning des chambres :

public class RoomVersion { 
    private int versionId;
}

public class Booking {
    private int roomVersionId;
}

Fonctionnement :

- Chaque modification de chambre crée une nouvelle version
- Les réservations référencent la version spécifique
- Pas de duplication : une seule source de vérité
