CREATE TABLE User (
    user_id         INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name       TEXT NOT NULL,
    email           TEXT UNIQUE NOT NULL,
    phone_number    TEXT UNIQUE NOT NULL,
    password        TEXT NOT NULL,
    gender          TEXT,
    ic_passport     TEXT UNIQUE NOT NULL
);

CREATE TABLE Branch (
    branch_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name        TEXT NOT NULL,
    location    TEXT NOT NULL,
    view        TEXT,
    distance    TEXT,
    price_range TEXT,
    description TEXT,
    rating      REAL
);

CREATE TABLE Room (
    room_id      INTEGER PRIMARY KEY AUTOINCREMENT,
    branch_id    INTEGER NOT NULL,
    room_type    TEXT NOT NULL,
    max_pax      INTEGER NOT NULL,
    amenities    TEXT NOT NULL,
    price        REAL NOT NULL,
    description  TEXT,
    FOREIGN KEY (branch_id) REFERENCES Branch(branch_id)
);

CREATE TABLE Booking (
    booking_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id      INTEGER NOT NULL,
    room_id      INTEGER NOT NULL,
    check_in     DATE NOT NULL,
    check_out    DATE NOT NULL,
    add_on_bed   BOOLEAN DEFAULT FALSE,
    total_price  REAL NOT NULL,
    status       TEXT DEFAULT 'Pending',
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (room_id) REFERENCES Room(room_id)
);

CREATE TABLE Payment (
    payment_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    booking_id   INTEGER NOT NULL,
    amount       REAL NOT NULL,
    payment_type TEXT NOT NULL,
    status       TEXT DEFAULT 'Pending',
    transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES Booking(booking_id)
);

CREATE TABLE Review (
    review_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id     INTEGER NOT NULL,
    branch_id   INTEGER NOT NULL,
    rating      REAL NOT NULL,
    comment     TEXT,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (branch_id) REFERENCES Branch(branch_id)
);
