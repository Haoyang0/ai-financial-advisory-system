## Requirements

Before running the project, make sure the following are installed:

- Node.js
- Java 17
- Ollama

---

## Installation

### 1. Clone the Repository
```bash
git clone https://github.com/Haoyang0/ai-financial-advisory-system.git
cd ai-financial-advisory-system

### 2. Install Frontend Dependencies
```bash
cd frontend
npm install

### 3. Install Ollama
https://ollama.com

### 4. Start Ollama
Open a terminal and run:
```bash
ollama serve

### 5. Download a Model
```bash
ollama pull llama3
You can check installed models with:
```bash
ollama list

### 6. run a Model
```bash
ollama run llama3
<img width="1054" height="431" alt="image" src="https://github.com/user-attachments/assets/ff8e9463-3b11-4c6e-8e42-8d4baea83c28" />


## 6. Run the Backend
```bash
cd backend
mvnw.cmd spring-boot:run
<img width="1919" height="992" alt="image" src="https://github.com/user-attachments/assets/92938016-708f-4bbe-b261-3b444f56e2b2" />


### 7. Run Frontend
```bash
cd frontend
npm run dev
<img width="1917" height="985" alt="image" src="https://github.com/user-attachments/assets/69f82c8f-f73d-491c-9d96-d728d8966433" />


The frontend usually runs at:
http://localhost:5173
<img width="1919" height="816" alt="image" src="https://github.com/user-attachments/assets/be2fbf44-bedd-4fed-a5f9-25b999dad77a" />

