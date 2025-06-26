# Handicapp

## The ultimate tool for calculating handicaps in polo

### Pre-requisites
- [Java 8 or higher](https://www.oracle.com/java/technologies/downloads/archive/)
- [PostgreSQL](https://www.postgresql.org/download/)
- [Maven](https://maven.apache.org/install.html)

### Installation

1. Clone this repository with the command:
   ```
   git clone https://github.com/Diegoacosta127/Handicapp
   ```
2. Move to the project directory:
   ```
   cd Handicapp
   ```

#### For Linux users

1. Create, if not exists, the `~/.pgpass` file

2. Add this line to the `~/.pgpass` file:
   ```
   localhost:5432:handicapp:{YOUR_POSTGRESQL_USERNAME}:{YOUR_POSTGRESQL_PASSWORD}
    ```
    Please make sure to replace `{YOUR_POSTGRESQL_USERNAME}` and `{YOUR_POSTGRESQL_PASSWORD}` with your actual PostgreSQL username and password.
3. Run the next command:
    ```
    chmod 600 ~/.pgpass
    ```
4. In the project folder, run the file `setup.sh`.

#### For Windows users
1. Create, if not exists, the `%APPDATA\postgresql\pgpass.conf` file

2. Add this line to the `pgpass.conf` file:
   ```
   localhost:5432:handicapp:{YOUR_POSTGRESQL_USERNAME}:{YOUR_POSTGRESQL_PASSWORD}
    ```
    Please make sure to replace `{YOUR_POSTGRESQL_USERNAME}` and `{YOUR_POSTGRESQL_PASSWORD}` with your actual PostgreSQL username and password.
3. In the project folder, run the file `setup.bat`.

### What Handicapp does?
Handicapp allows you to add:
- Players
    - Name
    - Last name
    - Mid name
    - Country of origin
- Season
    - Country
    - A description
    - Year
- Teams
    - Name
    - Season in which is playing
    - Players
    - Handicap for each player
- Matches
    - Season
    - Teams
    - Score for each player
    - A checkbox if it's the final match

Handicapp also allows you to edit:
- Player
- Team
- Season
- Country

Also you can export data to .pdf files:
- Seasons (only if the season is over)
- Teams (history of matches played in finished seasons)

Coming soon:
- Export data (players) to files

### How Handicapp works?
When a season is over (after a match is added as final), Handicapp calculates the handicap for each player in the season and generates the next year season with the same teams and its players, but with the new handicap for each player. Since handicap is actually given by a specialized committee appreciation and not by maths, I've developed the following rules:

- Weight score: this value multiplies each goal scored by each player, depending on their position:
    - $1.0$ for player #1
    - $1.3$ for player #2
    - $1.5$ for player #3
    - $1.7$ for player #4
- Weight punishment: this value penalizes all players for the total number of goals received, depending on their position:
    - $0.3$ for player #1
    - $0.5$ for player #2
    - $0.7$ for player #3
    - $1.0$ for player #4

$$
\begin{align}
    Match Balance = (Team Score - Rival Score) - (Team Handicap - Rival Handicap)
\end{align}
$$

$$
\begin{align}
    Season Balance = \sum_{i=1}^{n} (Match Balance)_i,\ n
\end{align}
$$

Where $n$ is the total of matches played in the season.

$$
\begin{align}
    Match Ratio = Rival Handicap / Team Handicap
\end{align}
$$

$$
\begin{align}Player Contribution = ((Individual Score * Weight Score) -\frac{Rival Score * Weight Score}4) * Match Ratio
\end{align}
$$

$$
\begin{align}
    Team Contribution = \sum_{i=1}^{4} Player Contribution_i
\end{align}
$$

With $Player Contribution$ and $Team Contribution$ we have:

$$
\begin{align}
    Proportional Contribution = \frac{Player Contribution}{Team Contribution} 
\end{align}
$$
The $Proportional Contribution$ will be $= 0.25$ if the $Team Contribution$ is $0$. The $Proportional Contribution$ will always be rounded to calculate only integers (as the handicap is).
$$
\begin{align}
    Handicap Variation = \frac{(Match Balance * Proportional Contribution) + (Season Balance * Proportional Contribution)}{2}
\end{align}
$$
And, finally, the new handicap is calculated as:
$$New Handicap = Initial Handicap + Handicap Variation$$

### Author
Developed entirely by Diego Acosta ([@diegoacosta127](https://github.com/Diegoacosta127)). If you have any questions, suggestions, contributions, or whatever, please feel free to contact me at my [website](https://diegoacosta127.pages.dev/).

### License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.