-- Tạo trước cả 2 database để các service không phải chờ nhau
CREATE DATABASE IF NOT EXISTS petstore_store
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS petstore_system
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
