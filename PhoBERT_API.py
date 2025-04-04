from fastapi import FastAPI
from pydantic import BaseModel
import torch
from transformers import RobertaForSequenceClassification, AutoTokenizer

# Tải mô hình và tokenizer PhoBERT
model = RobertaForSequenceClassification.from_pretrained("wonrax/phobert-base-vietnamese-sentiment")
tokenizer = AutoTokenizer.from_pretrained("wonrax/phobert-base-vietnamese-sentiment", use_fast=False)

# Khởi tạo ứng dụng FastAPI
app = FastAPI()

# Tạo lớp dữ liệu đầu vào
class SentenceInput(BaseModel):
    sentence: str

# API để phân tích cảm xúc
@app.post("/predict/")
@app.post("/predict")
async def predict_sentiment(input_data: SentenceInput):
    sentence = input_data.sentence

    # Mã hóa câu thành input_ids
    input_ids = torch.tensor([tokenizer.encode(sentence)])

    # Dự đoán cảm xúc
    with torch.no_grad():
        out = model(input_ids)
        sentiment_scores = out.logits.softmax(dim=-1).tolist()[0]
    
    # Lấy lớp có xác suất cao nhất
    sentiment_labels = ["NEG", "POS", "NEU"]
    max_score_index = sentiment_scores.index(max(sentiment_scores))
    predicted_label = sentiment_labels[max_score_index]
    
    # Trả về kết quả
    return {
        "prediction": predicted_label,
        "confidence": sentiment_scores[max_score_index]
    }
