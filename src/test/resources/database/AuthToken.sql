# AuthToken Table Definition
CREATE TABLE IF NOT EXISTS AuthToken (
    id      INT     PRIMARY KEY,                # User ID
    token   BLOB    UNIQUE          NOT NULL    # Encrypted AuthToken (serialized --> encrypted --> compressed)
)
    ROW_FORMAT = COMPRESSED                     # Compressed Row Format
    KEY_BLOCK_SIZE = 1                          # Key Block Size