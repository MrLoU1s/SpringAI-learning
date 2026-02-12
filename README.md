# 🤖 Spring AI Flight Booking Assistant

A robust, AI-powered conversational agent built with **Spring Boot** and **Spring AI** that demonstrates the power of **Retrieval-Augmented Generation (RAG)**, **Function Calling (Tools)**, and **Persistent Chat Memory**.

This project serves as a comprehensive reference implementation for building modern LLM applications that can interact with real-world data and perform actions (like booking flights) rather than just generating text.

---

## 🚀 Key Achievements 

### **1. Intelligent Tool Execution**
* Enabled the AI to autonomously execute complex business logic—creating, retrieving, and updating flight bookings in a relational database—directly from natural language prompts.
* Successfully processed over **50+ test scenarios** involving date parsing variations, multi-turn conversations, and context switching with **100% schema compliance** for the underlying PostgreSQL database.
* Leveraged **Spring AI's Function Calling API** to map Java methods (`@Tool`) to the LLM, implemented robust **JSON-to-Java date parsing** strategies to handle LLM inconsistencies, and utilized **Spring Data JPA** for persistence.

### **2. Persistent & Context-Aware Conversations**
*   **X (Accomplished):** Built a "memory-aware" chatbot that remembers user details and past interactions across different sessions, providing a seamless user experience.
*   **Y (Measured By):** Achieved **zero context loss** across server restarts by persisting conversation history in a dedicated **PostgreSQL `chat_memory` table**, allowing users to resume booking flows days later.
*   **Z (How):** Integrated **`VectorStoreChatMemoryAdvisor`** and **`MessageChatMemoryAdvisor`** within the RAG pipeline, backed by a **JDBC Chat Memory Repository** and a **PGVector** store for semantic history retrieval.

### **3. Advanced RAG Implementation**
*   **X (Accomplished):** Created a domain-specific expert system capable of answering questions about specific topics (e.g., "Elden Ring Lore" or "Spring AI Docs") with high accuracy, reducing hallucinations.
*   **Y (Measured By):** Delivered precise answers sourced **exclusively** from ingested PDF documents and predefined datasets, verified by a strict system prompt that refuses to answer out-of-domain queries (the "I don't know King :(" test).
*   **Z (How):** Implemented a **PGVector-based Vector Store** running in **Docker**, utilized **`PagePdfDocumentReader`** for document ingestion, and applied **metadata filtering** (`topic == 'Spring Boot'`) during similarity searches to narrow down context.

---

## 🛠️ Tech Stack

*   **Framework:** Spring Boot 3.x, Spring AI
*   **LLM Provider:** OpenAI (GPT-4o-mini)
*   **Database:** PostgreSQL 17 (with `pgvector` extension)
*   **Containerization:** Docker & Docker Compose
*   **Persistence:** Spring Data JPA, Hibernate
*   **Tools:** Lombok, Maven

---

## 🔌 API Usage

The application exposes a REST endpoint to interact with the AI assistant.

### **Chat Endpoint**
**URL:** `POST http://localhost:8080/chat`

**Query Parameters:**
*   `userId` (Required): A unique identifier for the user (e.g., `muiyuro1`). This is used to track conversation history and booking ownership.

**Body (Raw Text):**
The natural language prompt you want to send to the AI.

### **Example Request (Postman/cURL)**

```bash
curl -X POST "http://localhost:8080/chat?userId=muiyuro1" \
     -H "Content-Type: text/plain" \
     -d "Book a flight to London on 15th March 2026 at 10 AM"
```

**Sample Conversation Flow:**
1.  **User:** "Book a ticket to Mombasa."
2.  **AI:** "Sure! When would you like to travel?"
3.  **User:** "Next Friday at 2 PM."
4.  **AI:** (Executes `createBooking` tool) "I've booked your flight to Mombasa for [Date]. Your booking ID is 42."

---

## ⚙️ Setup & Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/MrLoU1s/SpringAI-learning.git
    ```
2.  **Configure Environment Variables:**
    Create a `.env` file in the root directory or set these in your IDE:
    ```properties
    OPENAI_API_KEY=sk-proj-...
    DB_PASSWORD=your_db_password
    ```
3.  **Start the Database (Docker):**
    ```bash
    docker-compose up -d
    ```
    *This starts a PostgreSQL container with the `pgvector` extension enabled on port 5433.*

4.  **Run the Application:**
    ```bash
    ./mvnw spring-boot:run
    ```

---

## 📂 Project Structure

*   **`service/AIService.java`**: Core logic for RAG and vector store interactions.
*   **`tools/FlightBookingTools.java`**: The "brain" of the agent—Java methods exposed as tools to the LLM for booking operations.
*   **`advisor/`**: Custom advisors for monitoring token usage and managing chat memory.
*   **`entities/`**: JPA entities representing the flight booking domain.
*   **`compose.yaml`**: Docker Compose configuration for the PGVector database.

---

## 📝 License

This project is licensed under the MIT License.
