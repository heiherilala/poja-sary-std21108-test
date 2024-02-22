CREATE TABLE picture_information (
    id VARCHAR(255) PRIMARY KEY,
    black_and_white_bucket_key VARCHAR(255),
    original_bucket_key VARCHAR(255),
    creation_datetime TIMESTAMP WITH TIME ZONE  DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_datetime TIMESTAMP WITH TIME ZONE  DEFAULT CURRENT_TIMESTAMP NOT NULL
);
