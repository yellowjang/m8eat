<template>
  <div class="mypage-container">
    <div class="mypage-header">
      <span class="mypage-icon">👤</span>
      <h1>마이페이지</h1>
      <p>내 건강 정보와 계정 정보를 한눈에 확인하고 관리해보세요.</p>
    </div>

    <div class="section">
      <h2>기본 정보</h2>
      <div class="basic-info">
        <div class="info-list">
          <span>
            <strong>이름:</strong>
            {{ user.name }}
          </span>
          <span>
            <strong>아이디:</strong>
            {{ user.id }}
          </span>
          <span>
            <strong>회원 유형:</strong>
            {{ user.role === "coach" ? "코치" : "일반 회원" }}
          </span>
        </div>
        <div class="edit-btn-wrapper">
          <button class="edit-btn" @click="showBasicEdit = true">수정</button>
        </div>
      </div>
    </div>

    <div class="section">
      <h2>나의 건강 정보</h2>
      <div class="info-cards">
        <div class="card" v-for="(value, key) in health" :key="key">
          <div class="card-title">{{ labelMap[key] }}</div>
          <div class="card-content">
            <template v-if="Array.isArray(value)">
              <span v-for="(item, index) in value" :key="index">
                {{ item }}
                <span v-if="index < value.length - 1">,</span>
              </span>
            </template>
            <template v-else>
              {{ value || "입력되지 않음" }}
            </template>
          </div>
        </div>
      </div>
      <div class="health-edit-wrapper">
        <button class="btn pink" @click="editHealth">전체 수정</button>
      </div>

      <!-- 채팅방 이동 버튼 -->
      <div class="chat-entry">
        <p>{{ partnerName }}의 채팅방으로 이동할 수 있어요.</p>
        <button class="chat-btn" @click="goToChat">채팅방 이동</button>
      </div>
      <div class="chat-spacing"></div>

      <div class="health-action-buttons">
        <button class="btn outline" @click="logout">로그아웃</button>
        <button class="btn gray" @click="withdraw">회원탈퇴</button>
      </div>
    </div>

    <div v-if="showBasicEdit" class="modal-backdrop">
      <div class="modal-content">
        <div class="modal-header">
          <h3>기본 정보 수정</h3>
          <span class="close-btn" @click="showBasicEdit = false">&times;</span>
        </div>
        <form @submit.prevent="saveBasicEdit">
          <label>이름</label>
          <input type="text" v-model="editableUser.name" />

          <label>아이디</label>
          <input type="text" v-model="editableUser.id" disabled />

          <label>회원 유형</label>
          <select v-model="editableUser.role">
            <option value="user">일반 회원</option>
            <option value="coach">코치</option>
          </select>

          <div class="modal-actions">
            <button type="submit" class="save-btn">저장</button>
            <button type="button" @click="showBasicEdit = false" class="cancel-btn">취소</button>
          </div>
        </form>
      </div>
    </div>

    <div v-if="showBasicEdit" class="modal-backdrop">
      <div class="modal-content">
        <div class="modal-header">
          <h3>기본 정보 수정</h3>
          <span class="close-btn" @click="showBasicEdit = false">&times;</span>
        </div>
        <form @submit.prevent="saveBasicEdit">
          <label>이름</label>
          <input type="text" v-model="editableUser.name" />

          <label>아이디</label>
          <input type="text" v-model="editableUser.id" disabled />

          <label>회원 유형</label>
          <select v-model="editableUser.role">
            <option value="user">일반 회원</option>
            <option value="coach">코치</option>
          </select>

          <div class="modal-actions">
            <button type="submit" class="save-btn">저장</button>
            <button type="button" @click="showBasicEdit = false" class="cancel-btn">취소</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 건강 정보 수정 모달 -->
    <div v-if="showHealthEdit" class="modal-backdrop">
      <div class="modal-content">
        <div class="modal-header">
          <h3>건강 정보 수정</h3>
          <span class="close-btn" @click="showHealthEdit = false">&times;</span>
        </div>
        <form @submit.prevent="saveHealthEdit">
          <label>키 (cm)</label>
          <input type="number" v-model.number="editableHealth.height" />

          <label>몸무게 (kg)</label>
          <input type="number" v-model.number="editableHealth.weight" />

          <label>질병</label>
          <div class="toggle-grid">
            <button
              v-for="item in illnessOptions"
              :key="item"
              type="button"
              class="toggle-btn"
              :class="{ active: editableHealth.illness.includes(item) }"
              @click="toggleSelection(editableHealth.illness, item)"
            >
              {{ item }}
            </button>
          </div>

          <label>알레르기</label>
          <div class="toggle-grid">
            <button
              v-for="item in allergyOptions"
              :key="item"
              type="button"
              class="toggle-btn"
              :class="{ active: editableHealth.allergy.includes(item) }"
              @click="toggleSelection(editableHealth.allergy, item)"
            >
              {{ item }}
            </button>
          </div>

          <label>목적</label>
          <select v-model="editableHealth.purpose">
            <option value="다이어트">다이어트</option>
            <option value="근육 증가">근육 증가</option>
            <option value="건강 유지">건강 유지</option>
            <option value="체력 향상">체력 향상</option>
          </select>

          <div class="modal-actions">
            <button type="submit" class="save-btn">저장</button>
            <button type="button" @click="showHealthEdit = false" class="cancel-btn">취소</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

const user = ref({ name: "홍길동", id: "hong123", role: "user" });
const editableUser = ref({ ...user.value });
const showBasicEdit = ref(false);

const saveBasicEdit = () => {
  user.value = { ...editableUser.value };
  showBasicEdit.value = false;
};

const labelMap = {
  height: "키 (cm)",
  weight: "몸무게 (kg)",
  illness: "질병",
  allergy: "알레르기",
  purpose: "목적",
};

const logout = () => {
  alert("로그아웃 처리");
};
const withdraw = () => {
  alert("회원탈퇴 처리");
};

const showHealthEdit = ref(false);

const health = ref({
  height: 170,
  weight: 60,
  illness: ["천식"],
  allergy: ["견과류"],
  purpose: "다이어트",
});
const illnessOptions = ["고혈압", "당뇨병", "심장병", "천식", "관절염"];
const allergyOptions = ["견과류", "우유", "계란", "갑각류", "밀"];

const editableHealth = ref({
  height: health.value.height,
  weight: health.value.weight,
  illness: [...health.value.illness],
  allergy: [...health.value.allergy],
  purpose: health.value.purpose,
});

const toggleSelection = (targetList, item) => {
  const index = targetList.indexOf(item);
  if (index > -1) targetList.splice(index, 1);
  else targetList.push(item);
};

const saveHealthEdit = () => {
  health.value.height = editableHealth.value.height;
  health.value.weight = editableHealth.value.weight;
  health.value.illness = [...editableHealth.value.illness];
  health.value.allergy = [...editableHealth.value.allergy];
  health.value.purpose = editableHealth.value.purpose;
  showHealthEdit.value = false;
};

const editHealth = () => {
  editableHealth.value = {
    height: health.value.height,
    weight: health.value.weight,
    illness: [...health.value.illness],
    allergy: [...health.value.allergy],
    purpose: health.value.purpose,
  };
  showHealthEdit.value = true;
};
const partnerName = ref(user.value.role === "coach" ? "회원들과" : "담당 코치와");

const goToChat = () => {
  router.push("/chat");
};
</script>

<style lang="scss" scoped>
.mypage-container {
  padding: 2rem;
  background-color: #fdeeee;
  font-family: "Noto Sans KR", sans-serif;
  min-height: 100vh;
  text-align: center;

  .mypage-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 2rem;

    .mypage-icon {
      font-size: 2.5rem;
      margin-bottom: 0.5rem;
    }

    h1 {
      font-size: 2rem;
      font-weight: bold;
      margin-bottom: 0.2rem;
    }

    p {
      font-size: 1rem;
      color: #666;
    }
  }

  .section {
    text-align: left;
    max-width: 1024px;
    margin: 0 auto;

    h2 {
      font-size: 1.4rem;
      margin: 2rem 0 1rem;
    }
  }

  .basic-info {
    background: white;
    padding: 1.5rem 2rem;
    border-radius: 12px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
    gap: 1rem;

    .info-list {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
      font-size: 1rem;

      span {
        display: flex;
        gap: 0.5rem;

        strong {
          width: 80px;
          color: #555;
        }
      }
    }

    .edit-btn-wrapper {
      display: flex;
      justify-content: flex-end;

      .edit-btn {
        background-color: #de9c9c;
        color: white;
        border: none;
        padding: 0.4rem 1rem;
        font-size: 0.9rem;
        border-radius: 6px;
        font-weight: bold;
        cursor: pointer;
      }
    }
  }

  .info-cards {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: 1rem;

    .card {
      background: white;
      border-radius: 12px;
      padding: 1rem;
      box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);

      .card-title {
        font-size: 0.9rem;
        color: #999;
        margin-bottom: 0.3rem;
      }

      .card-content {
        font-size: 1.2rem;
        font-weight: 600;
        margin-bottom: 0.5rem;
      }
    }
  }

  .health-edit-wrapper {
    margin-top: 1rem;
    display: flex;
    justify-content: center;
  }

  .health-action-buttons {
    margin-top: 1rem;
    display: flex;
    justify-content: center;
    gap: 1rem;
  }

  .btn {
    padding: 0.5rem 1.2rem;
    font-size: 0.9rem;
    font-weight: bold;
    border-radius: 6px;
    cursor: pointer;
    border: none;
    &.pink {
      background-color: #de9c9c;
      color: white;
    }
    &.outline {
      background-color: white;
      color: #de9c9c;
      border: 1px solid #de9c9c;
    }
    &.gray {
      background-color: #888;
      color: white;
    }
  }

  .modal-backdrop {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 10;
  }

  .modal-content {
    background: white;
    padding: 2rem;
    border-radius: 12px;
    width: 90%;
    max-width: 480px;
  }

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;

    .close-btn {
      font-size: 1.5rem;
      cursor: pointer;
    }
  }

  .modal-content input,
  .modal-content select {
    width: 100%;
    padding: 0.8rem;
    border: 1px solid #ccc;
    border-radius: 10px;
    margin-bottom: 1rem;
    font-size: 0.95rem;
  }

  .modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;

    .save-btn {
      background-color: #de9c9c;
      color: white;
      border: none;
      padding: 0.6rem 1rem;
      border-radius: 6px;
      cursor: pointer;
      font-weight: bold;
    }

    .cancel-btn {
      background-color: #ccc;
      color: white;
      border: none;
      padding: 0.6rem 1rem;
      border-radius: 6px;
      cursor: pointer;
      font-weight: bold;
    }
  }
}
.toggle-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.toggle-btn {
  padding: 0.4rem 0.8rem;
  border-radius: 20px;
  border: 1px solid #ccc;
  background-color: #fff;
  cursor: pointer;
  font-size: 0.9rem;
}

.toggle-btn.active {
  background-color: #de9c9c;
  color: white;
  border-color: #de9c9c;
}

.chat-entry {
  margin-top: 3rem;
  text-align: center;

  p {
    margin-bottom: 0.8rem;
    font-weight: 500;
  }

  .chat-btn {
    padding: 0.6rem 1.4rem;
    background-color: #de9c9c;
    color: white;
    border: none;
    border-radius: 6px;
    font-weight: bold;
    cursor: pointer;
    transition: 0.2s;
  }

  .chat-btn:hover {
    background-color: #d67b7b;
  }
}
.chat-spacing {
  margin-bottom: 2.5rem;
}
</style>
