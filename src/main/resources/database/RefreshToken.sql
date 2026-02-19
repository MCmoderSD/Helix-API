# RefreshToken Table Definition
CREATE TABLE IF NOT EXISTS RefreshToken
(
    id      INT     PRIMARY KEY,                # User ID
    token   TEXT    UNIQUE          NOT NULL    # Refresh Token (encrypted)
)
    ROW_FORMAT = COMPRESSED                     # Compressed Row Format
    KEY_BLOCK_SIZE = 1                          # Key Block Size