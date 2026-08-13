# Local Docker secrets

Create these two untracked files before starting the containers:

- `postgres_password.txt`
- `minio_root_password.txt`
- `jwt_secret.txt` (Base64-encoded, at least 32 random bytes)

Each file must contain only its secret value. Do not commit these files.
