# 🌱 LUIS V2 – Backend

The backend of **LUIS V2** (Air Quality Information System V2) provides the server-side logic for a web-based application that makes air quality data from the Environmental Office of Styria accessible, analyzable, and exportable.

## 🚀 Project Goal

The aim is to offer a user-friendly platform that processes local air quality data (e.g., particulate matter, nitrogen dioxide, ozone) from the region of Styria and makes it available in various formats (e.g., CSV, JSON) – for data analysts, environmental authorities, educators, and the general public.

---

## 🔧 Technologies

- **Programming Language**: Java 21
- **Framework**: SpringBoot
- **Data Sources**: Environmental Office of Styria (via API or CSV)


---

## 📁 API Endpoints (Example)

```http
GET /api/v1/data                  # Retrieve air quality data
GET /api/v1/data/export?format=csv
GET /api/v1/forecast              # Forecast values (ML model)
```

> Detailed API documentation available at `/docs` (Swagger UI with FastAPI)

---

## 🤝 Contributing

Pull requests are welcome! Please open an issue first if you want to make major changes.  
Make sure to test your code and follow PEP8 formatting.

---

## 📄 License

MIT License – see [LICENSE](LICENSE)

---

## 🧠 Contact & Team

**Project Lead:** Tobias Enthaler  
**Team:** Jakob Hanner, Achmad Inalov  
**Project Name:** LUIS V2  
**Contact:** *[GitHub Issues](https://github.com/luis-v2/luis-v2-backend/issues)*
