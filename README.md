# 🚀 AI Chatbot Platform (Mini RAG System)

A modular AI-powered chatbot platform built with Spring Boot, implementing a production-style **Retrieval-Augmented Generation (RAG)** pipeline.

---

## ✨ Features

* ✅ Vector Search (pgvector)
* ✅ Embedding generation (OpenAI / Fake client)
* ✅ Reranking
* ✅ Semantic cache
* ✅ Chat history persistence (MongoDB)
* ✅ Course & FAQ knowledge base
* ✅ OCR module
* ✅ Intent detection
* ✅ Orchestration layer

---

## 🧠 Architecture Overview

The system follows a simplified RAG architecture:

```
User Question
      ↓
Chat Controller
      ↓
Orchestrator
      ↓
Intent Detection
      ↓
Embedding Generation
      ↓
Vector Similarity Search (PostgreSQL + pgvector)
      ↓
Reranking
      ↓
Semantic Cache Check
      ↓
LLM Generation
      ↓
Response + Chat History Storage
```

---

## 🏗 Tech Stack

### Backend

* Java 21
* Spring Boot
* Maven
* WebClient

### Databases

* PostgreSQL + pgvector (vector search)
* MongoDB (chat history & OCR storage)

### AI Components

* OpenAI API integration
* Embedding service
* Rerank service
* Intent classification
* Semantic caching

---

## 📂 Project Modules

### 1️⃣ Core AI Modules

| Module    | Description                |
| --------- | -------------------------- |
| Embedding | Converts text into vectors |
| QA Search | Vector similarity search   |
| Rerank    | Re-ranks search candidates |
| Intent    | Detects user intent        |
| Cache     | Semantic caching layer     |

---

### 2️⃣ Business Modules

| Module | Description              |
| ------ | ------------------------ |
| Course | Course management        |
| Chat   | Chat session management  |
| OCR    | Image-to-text processing |

---

### 3️⃣ Infrastructure

* OpenAI configuration
* Cache configuration
* WebClient configuration
* Repository configuration
* Flyway migrations

---

## 🗄 Database Design

### PostgreSQL

**Tables:**

* courses
* course_details
* qa_pairs
* embeddings
* chatlog_feedback

**Used for:**

* Domain data
* Vector search
* AI knowledge retrieval

---

### MongoDB

**Collections:**

* chat_sessions
* messages
* ocr_images
* semantic_cache

**Used for:**

* Chat memory
* OCR results
* Fast response reuse

---

## 🔍 Features in Detail

* ✅ Semantic search with pgvector
* ✅ RAG pipeline
* ✅ LLM fallback
* ✅ Intent-aware routing
* ✅ Semantic caching
* ✅ Modular architecture
* ✅ Fake & Real client switching (great for testing)

---

## 🧪 Testing

Unit tests included for:

* FakeEmbeddingClient
* FakeRerankClient
* IntentClient
* Application context

### Run tests

```bash
mvn test
```

---

## 🐳 Running the Project

### 1️⃣ Start databases

```bash
docker-compose up -d
```

### 2️⃣ Run application

```bash
mvn spring-boot:run
```

---

## 📌 What This Project Demonstrates

* Production-style RAG architecture
* Clean modular Spring Boot design
* AI pipeline orchestration
* Vector similarity search
* Hybrid architecture (AI + relational DB + document DB)

---

## 🔮 Future Improvements

* Observability & monitoring
* Rate limiting
* Prompt management layer
* Streaming responses
* Multi-tenant support
* Evaluation & feedback loop

---

## 👨‍💻 Author

Built as an AI-powered modular chatbot platform for learning and experimentation with real-world LLM architecture.
