# Path to Programmer

![Path to Programmer icon](src/main/resources/icon.svg)

Path to Programmer is a JavaFX career simulation game created for an AVID project. The game is based on the journey to becoming a programmer, and it challenges the player to make decisions that affect their progress over time.

Throughout the game, the player answers interactive questions about school, learning, projects, work, and balance. Each answer changes the player’s stats and leads them toward different outcomes.

## Features

- JavaFX desktop application
- Title screen and rules screen
- 20 decision-based stages
- Progress bar and stat tracking
- Scene images for different parts of the journey
- Multiple end results based on player choices
- Source-code friendly project structure

## Stats

The game tracks three main values:

- **Skill** — practical coding ability
- **Knowledge** — understanding and learning progress
- **Burnout** — stress and overwork

The player’s goal is to improve Skill and Knowledge while keeping Burnout under control.

## How to Play

Start the game from the title screen and read each prompt carefully.  
At every stage, choose one of the two available options.  
Each choice changes your stats and moves you to the next part of the journey.

Your decisions shape the final result.

## Project Purpose

This project was created as an interactive way to present information about the programming career path. Instead of a slideshow or essay, the project turns the idea into a playable experience.

It is meant to show that becoming a programmer involves:

- learning consistently
- building projects
- solving problems
- managing stress
- continuing to grow over time

## Technologies Used

- **Java 21**
- **JavaFX**
- **Gradle**
- **Kotlin DSL**

## Project Structure

```text
src/
  main/
    java/
      dev/joshuatic/avidcareerproject/
    resources/
      images/
        # Contains all images.
      icon.svg
      style.css
```

### THERE IS NO INVOLVEMENT OF C/C++ IN THIS GAME. IT IS SOLELY JAVA AND IS A CODEBASE PERCENTAGE FOR THE BUNDLED JAVA RUNTIME

# Running
Run the project with the bundled Gradle wrapper:

### You need the Java 21 JDK installed, and specifically Java 21.
```bash
./gradlew run
```

Building the project

### You need the Java 21 JDK installed.

```bash
./gradlew build
```

Running the project.
Run the project with the bundled .bat file.

## Author:
Joshua Murr

## License
This project is licensed under the [MIT License](LICENSE)