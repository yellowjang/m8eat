from fastapi import FastAPI, File, UploadFile
from ultralytics import YOLO
from PIL import Image
import numpy as np
import io

app = FastAPI()
model = YOLO("yolov8n.pt")  # 기본 작은 모델 사용

@app.post("/detect")
async def detect_food(file: UploadFile = File(...)):
    contents = await file.read()
    image = Image.open(io.BytesIO(contents)).convert("RGB")
    np_image = np.array(image)


    results = model(np_image)[0]
    boxes = []

    print("YOLO 결과:", results)
    print("박스 수:", len(results.boxes))

    for box in results.boxes:
        # cls_id = int(box.cls[0])
        # label = model.names[cls_id]
        if box.cls is not None:
            cls_id = int(box.cls[0].item())
            label = model.names.get(cls_id, "unknown")
        else:
            label = "unknown"

        confidence = float(box.conf[0])
        # x1, y1, x2, y2 = map(int, box.xyxy[0])
        x1, y1, x2, y2 = [int(coord.item()) for coord in box.xyxy[0]]

        w = x2 - x1
        h = y2 - y1

        if confidence >= 0.4:
            boxes.append({
                "label": label,
                "confidence": confidence,
                "x": x1,
                "y": y1,
                "w": w,
                "h": h
            })

    return {"boxes": boxes}
