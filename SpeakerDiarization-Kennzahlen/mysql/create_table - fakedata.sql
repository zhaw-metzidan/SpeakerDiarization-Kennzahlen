CREATE SCHEMA `fakedata` ;


CREATE TABLE IF NOT EXISTS `fakedata`.`session` (
  `idSession` INT NOT NULL AUTO_INCREMENT,
  `sessionTimeTalk` INT NULL DEFAULT NULL,
  `totalSpeeches` INT NULL DEFAULT NULL,
  `totalAverageSpeechTime` DOUBLE NULL DEFAULT NULL,
  `averageSpeechcountPerSpeaker` DOUBLE NULL DEFAULT NULL,
  PRIMARY KEY (`idSession`))
ENGINE = InnoDB
AUTO_INCREMENT = 18
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `fakedata`.`speaker` (
  `idspeaker` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL DEFAULT NULL,
  `totalSpeechCount` INT NULL DEFAULT NULL,
  `totalTimeTalk` INT NULL DEFAULT NULL,
  `totalTimeSilent` INT NULL DEFAULT NULL,
  `longestSpeech` INT NULL DEFAULT NULL,
  `shortestSpeech` INT NULL DEFAULT NULL,
  `averageSpeechTime` DOUBLE NULL DEFAULT NULL,
  `averageSimilarity` DOUBLE NULL DEFAULT NULL,
  PRIMARY KEY (`idspeaker`))
ENGINE = InnoDB
AUTO_INCREMENT = 42
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `fakedata`.`speech` (
  `idspeech` INT NOT NULL AUTO_INCREMENT,
  `speechStart` INT NULL DEFAULT NULL,
  `speechTime` INT NULL DEFAULT NULL,
  `speechEnd` INT NULL DEFAULT NULL,
  `similarity` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idspeech`))
ENGINE = InnoDB
AUTO_INCREMENT = 194
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `fakedata`.`session_speaker` (
  `id_session` INT NULL DEFAULT NULL,
  `id_speaker` INT NULL DEFAULT NULL,
  INDEX `id_session_idx` (`id_session` ASC) VISIBLE,
  INDEX `id_speaker_idx` (`id_speaker` ASC) VISIBLE,
  CONSTRAINT `id_session`
    FOREIGN KEY (`id_session`)
    REFERENCES `fakedata`.`session` (`idSession`),
  CONSTRAINT `id_speaker`
    FOREIGN KEY (`id_speaker`)
    REFERENCES `fakedata`.`speaker` (`idspeaker`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `fakedata`.`speaker_timeline` (
  `id_previous_speaker` INT NULL DEFAULT NULL,
  `id_current_speaker` INT NULL DEFAULT NULL,
  INDEX `id_previous_speaker_idx` (`id_previous_speaker` ASC) VISIBLE,
  INDEX `id_current_speaker_idx` (`id_current_speaker` ASC) VISIBLE,
  CONSTRAINT `id_current_speaker`
    FOREIGN KEY (`id_current_speaker`)
    REFERENCES `fakedata`.`speaker` (`idspeaker`),
  CONSTRAINT `id_previous_speaker`
    FOREIGN KEY (`id_previous_speaker`)
    REFERENCES `fakedata`.`speaker` (`idspeaker`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `fakedata`.`speaker_speech` (
  `id_speaker` INT NULL DEFAULT NULL,
  `id_speech` INT NULL DEFAULT NULL,
  INDEX `id_speaker_idx` (`id_speaker` ASC) VISIBLE,
  INDEX `id_speech_idx` (`id_speech` ASC) VISIBLE,
  CONSTRAINT `id_speakerOfSpeech`
    FOREIGN KEY (`id_speaker`)
    REFERENCES `fakedata`.`speaker` (`idspeaker`),
  CONSTRAINT `id_speech`
    FOREIGN KEY (`id_speech`)
    REFERENCES `fakedata`.`speech` (`idspeech`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;