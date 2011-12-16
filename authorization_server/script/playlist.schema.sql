USE ms_authorization;

DROP TABLE IF EXISTS playlists;
CREATE TABLE playlists (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  user_id INTEGER NOT NULL,
  UNIQUE KEY (title, user_id)
) TYPE=INNODB;

DROP TABLE IF EXISTS playlist_entries;
CREATE TABLE playlist_entries (
  playlist_id INTEGER NOT NULL,
  entry_id VARCHAR(100) NOT NULL,
  order_num INTEGER NOT NULL,
  PRIMARY KEY (playlist_id, entry_id, order_num),
  FOREIGN KEY (playlist_id) REFERENCES playlists(id) ON DELETE CASCADE  ON UPDATE CASCADE
) TYPE=INNODB;
