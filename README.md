# 🛡️ Insurance Core System - Take Home Test

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.1-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

> Dokumentasi ini berisi skema database, penjelasan entitas, dan panduan pengujian REST API untuk sistem administrasi polis asuransi.

Proyek ini dibangun menggunakan **Java 21**, **Spring Boot 4.1.1**, **Spring Web**, dan database **MySQL**, dengan memanfaatkan fitur **Java Records** untuk *immutability* DTO (Data Transfer Object).


---

## Teknologi yang Digunakan
- **Bahasa Pemrograman:** Java 21
- **Framework Utama:** Spring Boot 4.1.1, Spring Web
- **Database:** MySQL
- **Arsitektur Data:** Java Records (Immutable DTO)

---

## 1. Skema Database & Relasi Tabel

Sistem ini menggunakan desain database relasional untuk mengelola kontrak asuransi, data profil nasabah, dan pencocokan identitas e-KYC. Berikut adalah penjelasan masing-masing entitas dalam sistem:

| Nama Tabel                   | Deskripsi & Fungsi Utama |
|:-----------------------------| :--- |
| `TPOLICYS`                   | Tabel master tingkat teratas yang menyimpan atribut kontrak dasar polis (seperti Nomor Polis, Tanggal Submit, Mata Uang, dan Kode Produk Dasar). |
| `TPOLICYS_INFO_ID`           | Tabel ekstensi operasional dari `TPOLICYS` untuk menyimpan data pelengkap seperti nomor aplikasi dan flag cetak e-Policy. |
| `TCLIENT_DETAILS`            | Tabel master (*Single Source of Truth*) untuk data demografis nasabah, meliputi Nama, Tanggal Lahir, KTP, dan Jenis Kelamin. |
| `TCLIENT_POLICY_LINKS`       | Tabel jembatan (*Junction Table*) yang mendefinisikan peran (`LINK_TYP`) setiap nasabah di dalam polis, seperti *Policy Owner* (`O`), *Insured* (`I`), atau *Payor* (`P`). |
| `TCOVERAGES`                 | Tabel yang menyimpan detail pertanggungan perlindungan (Asuransi Dasar & Rider tambahan). |
| `TCLIENT_DUKCAPIL_DETAILS` | Tabel log/response yang menyimpan hasil validasi identitas nasabah terhadap sistem catatan sipil (Dukcapil). |

### Representasi Relasi Tabel
Berikut adalah alur relasi antar tabel di dalam database:

```text
[ TPOLICYS ] (Master Polis)
  │
  ├── (One-to-One)  ──> [ TPOLICYS_INFO_ID ] (Info tambahan e-Policy, dll)
  │
  ├── (One-to-Many) ──> [ TCLIENT_POLICY_LINKS ] (Menghubungkan Polis dengan peran Nasabah)
  │
  └── (One-to-Many) ──> [ TCOVERAGES ] (Data pertanggungan dan Rider Polis)


[ TCLIENT_DETAILS ] (Master Nasabah / Single Source of Truth)
  │
  ├── (One-to-One)  ──> [ TCLIENT_DUKCAPIL_DETAILS ] (Log validasi e-KYC opsional)
  │
  ├── (One-to-Many) ──> [ TCLIENT_POLICY_LINKS ] (Menghubungkan Nasabah dengan Polis)
  │
  └── (One-to-Many) ──> [ TCOVERAGES ] (Pertanggungan spesifik untuk Nasabah ini)
```

---

## 2. Inisialisasi Database (DDL & DML)

Untuk menjalankan proyek ini secara lokal, Anda perlu membuat struktur tabel beserta relasinya (DDL) dan memasukkan data awal/dummy (DML).

Jalankan script SQL di bawah ini pada database MySQL Anda:

### A. Data Definition Language (DDL) - Pembuatan Tabel
Jalankan script ini terlebih dahulu untuk membangun skema tabel dan relasi (*Foreign Key*).

```sql
-- Cretae Database
CREATE DATABASE IF NOT EXISTS insurance;

-- Create Tables
CREATE TABLE TPOLICYS (
    POL_NUM VARCHAR(20) NOT NULL,
    DIST_CHNL_CD VARCHAR(10),
    POL_RMRK VARCHAR(255),
    SBMT_DT DATE,
    CRCY_CODE CHAR(3),
    PLAN_CODE_BASE VARCHAR(20),
    AGT_CODE VARCHAR(20),
    PRIMARY KEY (POL_NUM)
);

CREATE TABLE TCLIENT_DETAILS (
    CLI_NUM VARCHAR(20) NOT NULL,
    CLI_NM VARCHAR(100) NOT NULL,
    BIRTH_DT DATE NOT NULL,
    ID_NUM VARCHAR(20) NOT NULL,
    SEX_CODE CHAR(1) NOT NULL COMMENT 'M=Male, F=Female',
    PRIMARY KEY (CLI_NUM)
);

CREATE TABLE TCLIENT_POLICY_LINKS (
    POL_NUM VARCHAR(20) NOT NULL,
    CLI_NUM VARCHAR(20) NOT NULL,
    LINK_TYP CHAR(1) NOT NULL COMMENT 'O=Owner, I=Insured, P=Payor, T=Other Insured, W=Beneficial Owner',
    REL_TO_INSRD VARCHAR(5) COMMENT 'SE=Self, SP=Spouse, CH=Child',
    ADDR_TYP VARCHAR(5) COMMENT 'HOME, OFFC',
    PRIMARY KEY (POL_NUM, CLI_NUM, LINK_TYP),
    CONSTRAINT fk_link_policy FOREIGN KEY (POL_NUM) REFERENCES TPOLICYS(POL_NUM) ON DELETE CASCADE,
    CONSTRAINT fk_link_client FOREIGN KEY (CLI_NUM) REFERENCES TCLIENT_DETAILS(CLI_NUM) ON DELETE CASCADE
);

CREATE TABLE TCLIENT_DUKCAPIL_DETAILS (
    CLI_NUM VARCHAR(20) NOT NULL,
    ID_NUM_RESULT VARCHAR(20) COMMENT 'MATCH, NOT_MATCH',
    CLI_NM_RESULT VARCHAR(20) COMMENT 'MATCH, NOT_MATCH, PARTIAL',
    BIRTH_DT_RESULT VARCHAR(20) COMMENT 'MATCH, NOT_MATCH',
    SEX_CODE_RESULT VARCHAR(20) COMMENT 'MATCH, NOT_MATCH',
    SUMMARY_RESULT VARCHAR(100) COMMENT 'Deskripsi hasil validasi',
    SUMMARY_CODE VARCHAR(10) COMMENT 'Kode validasi internal',
    RESULT_CODE VARCHAR(10) COMMENT '00=Success, 99=Failed',
    CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    LASTUPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (CLI_NUM),
    CONSTRAINT fk_dukcapil_client FOREIGN KEY (CLI_NUM) REFERENCES TCLIENT_DETAILS(CLI_NUM) ON DELETE CASCADE
);

CREATE TABLE TPOLICYS_INFO_ID (
    POL_NUM VARCHAR(20) NOT NULL,
    APP_NUM VARCHAR(30) COMMENT 'Nomor SPAJ (Surat Pengajuan Asuransi)',
    BILL_CAT VARCHAR(20) COMMENT 'Kategori Penagihan (misal: CC, DEBIT, CASH)',
    ISS_EPOL CHAR(1) COMMENT 'Flag cetak e-Policy (Y/N)',
    FS_CODE VARCHAR(10) COMMENT 'Fund Source Code / Sumber Dana',
    PRIMARY KEY (POL_NUM),
    CONSTRAINT fk_polinfo_policy FOREIGN KEY (POL_NUM) REFERENCES TPOLICYS(POL_NUM) ON DELETE CASCADE
);

CREATE TABLE TCOVERAGES (
    POL_NUM VARCHAR(20) NOT NULL,
    PLAN_CODE VARCHAR(20) NOT NULL COMMENT 'Kode Produk Asuransi',
    VERS_NUM INT NOT NULL COMMENT 'Versi Plan',
    CVG_EFF_DT DATE NOT NULL COMMENT 'Tanggal Mulai Berlaku Coverage',
    CLI_NUM VARCHAR(20) NOT NULL COMMENT 'Tertanggung dari Coverage ini',
    CVG_TYP CHAR(1) COMMENT 'B = Base (Dasar), R = Rider (Tambahan)',
    CVG_PREM DECIMAL(15, 2) COMMENT 'Premi per coverage',
    CVG_STAT_CD VARCHAR(5) COMMENT 'INFOR = Inforce, LAPS = Lapsed',
    
    PRIMARY KEY (POL_NUM, PLAN_CODE, VERS_NUM, CVG_EFF_DT, CLI_NUM),
    
    CONSTRAINT fk_cvg_policy FOREIGN KEY (POL_NUM) REFERENCES TPOLICYS(POL_NUM) ON DELETE CASCADE,
    CONSTRAINT fk_cvg_client FOREIGN KEY (CLI_NUM) REFERENCES TCLIENT_DETAILS(CLI_NUM) ON DELETE CASCADE
);
```

### B. Data Manipulation Language (DML) - Insert Dummy Data
Setelah tabel berhasil dibuat, jalankan script ini untuk memasukkan data uji coba.

```sql
INSERT INTO TPOLICYS (POL_NUM, DIST_CHNL_CD, POL_RMRK, SBMT_DT, CRCY_CODE, PLAN_CODE_BASE, AGT_CODE) VALUES
    ('123', 'AGENCY', 'Polis Baru Suami Istri', '2023-01-15', 'IDR', 'TERM_LIFE', 'AGT001'),
    ('124', 'BANCASS', 'Polis Individu', '2023-02-20', 'USD', 'UNIT_LINK', 'AGT002');

INSERT INTO TCLIENT_DETAILS (CLI_NUM, CLI_NM, BIRTH_DT, ID_NUM, SEX_CODE) VALUES
    ('CLI-001', 'Budi Santoso', '1985-05-12', '3171234567890001', 'M'),
    ('CLI-002', 'Siti Aminah', '1987-08-25', '3171234567890002', 'F'),
    ('CLI-003', 'Andi Darmawan', '1990-11-02', '3271234567890003', 'M');

INSERT INTO TCLIENT_POLICY_LINKS (POL_NUM, CLI_NUM, LINK_TYP, REL_TO_INSRD, ADDR_TYP) VALUES
    ('123', 'CLI-001', 'O', 'SP', 'HOME'),
    ('123', 'CLI-001', 'P', 'SP', 'HOME'),
    ('123', 'CLI-002', 'I', 'SE', 'HOME'),
    ('124', 'CLI-003', 'O', 'SE', 'OFFC'),
    ('124', 'CLI-003', 'I', 'SE', 'OFFC');

INSERT INTO 
    TCLIENT_DUKCAPIL_DETAILS (CLI_NUM, ID_NUM_RESULT, CLI_NM_RESULT, BIRTH_DT_RESULT, SEX_CODE_RESULT, SUMMARY_RESULT, SUMMARY_CODE, RESULT_CODE)
VALUES
    ('CLI-002', 'MATCH', 'MATCH', 'MATCH', 'MATCH', 'ALL DATA MATCHED', 'M001', '00');

INSERT INTO TPOLICYS_INFO_ID (POL_NUM, APP_NUM, BILL_CAT, ISS_EPOL, FS_CODE) VALUES
    ('123', 'APP-2023-0001', 'AUTODEBET', 'Y', 'GAJI'),
    ('124', 'APP-2023-0002', 'KARTUKREDIT', 'Y', 'BISNIS');

INSERT INTO TCOVERAGES (POL_NUM, PLAN_CODE, VERS_NUM, CVG_EFF_DT, CLI_NUM, CVG_TYP, CVG_PREM, CVG_STAT_CD) VALUES
    ('123', 'TERM_LIFE', 1, '2023-01-15', 'CLI-002', 'B', 500000.00, 'INFOR'),
    ('123', 'HEALTH_RIDER', 1, '2023-01-15', 'CLI-002', 'R', 250000.00, 'INFOR'),
    ('124', 'UNIT_LINK', 1, '2023-02-20', 'CLI-003', 'B', 1000000.00, 'INFOR');
```

---

## 3. Dokumentasi REST API (Postman)

Bagian ini berisi panduan untuk menguji endpoint REST API menggunakan **Postman**.

### Prasyarat Konfigurasi
- **Base URL:** `http://localhost:8090` *(Pastikan server berjalan pada port 8090)*
- **Headers:** Saya menggunakan Custom Headers untuk keperluan internal logging. Tambahkan konfigurasi berikut di tab **Headers** pada Postman (bersifat opsional tapi direkomendasikan):
    - `Accept`: `application/json`
    - `X-User-ID`: `20260001`
    - `X-Correlation-ID`: `{{$randomUUID}}` *(Gunakan fitur dynamic variable Postman untuk men-generate UUID otomatis)*

---

### A. Get Policy Owner
Mengambil data detail nasabah yang berstatus sebagai **Policy Owner** (`LINK_TYP = 'O'`) pada polis tertentu.

- **Method:** `GET`
- **URL:** `http://localhost:8090/api/v1/policies/123/owner`
- **cURL Equivalent:**
  ```bash
  curl -X GET "http://localhost:8090/api/v1/policies/123/owner" \
       -H "Accept: application/json" \
       -H "X-User-ID: 20260001" \
       -H "X-Correlation-ID: {{$randomUUID}}"
  ```
  - **Response**
    ```json
    {
      "meta": {
        "success": true,
        "code": 200,
        "correlation_id": "b21e9386-de7d-4e7b-991e-5392b735cacd",
        "message": "Client retrieved successfully"
      },
      "data": [
        {
            "client_number": "CLI-001",
            "client_name": "Budi Santoso",
            "birth_date": "1985-05-12",
            "id_number": "3171234567890001",
            "gender_code": "M"
        }
      ]
    }
    ```

### B. Get Insured Dukcapil Data
Mengambil data validasi Dukcapil (e-KYC) dari nasabah yang berstatus sebagai pihak yang diasuransikan (Insured / LINK_TYP = 'I') pada polis tertentu.

- **Method:** `GET`
- **URL:** `http://localhost:8090/api/v1/policies/123/insured/dukcapil`
- **cURL Equivalent:**
  ```bash
  curl -X GET "http://localhost:8090/api/v1/policies/123/insured/dukcapil" \
       -H "Accept: application/json" \
       -H "X-User-ID: 20260001" \
       -H "X-Correlation-ID: {{$randomUUID}}"
  ```
  - **Response**
    ```json
    {
        "meta": {
            "success": true,
            "code": 200,
            "correlation_id": "e8690ba1-bd79-419a-9043-766b05dede0a",
            "message": "Client Dukcapil retrieved successfully"
        },
        "data": [
          { 
            "client_number": "CLI-002",
            "id_match_status": "MATCH",
            "name_match_status": "MATCH",
            "birth_date_match_status": "MATCH",
            "gender_match_status": "MATCH",
            "summary": "ALL DATA MATCHED",
            "status": "00"
          }
        ]
    }
    ```