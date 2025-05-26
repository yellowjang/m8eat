<template>
  <div class="signup-container">
    <!-- <h3>회원가입</h3> -->
    <h3>회원가입 ({{ userType === "coach" ? "코치" : "일반 회원" }})</h3>
    <div class="signup-box">
      <!-- <form class="signup-form" @submit.prevent="signup">
        <label for="name">이름</label>
        <input id="name" type="text" placeholder="이름을 입력해주세요." v-model.trim="name" />

        <label for="userId">아이디</label>
        <input id="userId" type="text" placeholder="아이디를 입력해주세요." v-model.trim="id" />

        <label for="password">비밀번호</label>
        <input id="password" type="password" placeholder="비밀번호를 입력해주세요." v-model.trim="password" />

        <label for="passwordConfirm">비밀번호 확인</label>
        <input id="passwordConfirm" type="password" placeholder="비밀번호를 한 번 더 입력해주세요." v-model.trim="passwordConfirm" />

        <button type="submit" class="signup-button">회원가입</button>
      </form> -->
      <form class="signup-form" @submit.prevent="signup">
        <label for="name">이름</label>
        <input id="name" type="text" placeholder="이름을 입력해주세요." v-model.trim="name" />

        <label for="userId">아이디</label>
        <input id="userId" type="text" placeholder="아이디를 입력해주세요." v-model.trim="id" />

        <label for="password">비밀번호</label>
        <input id="password" type="password" placeholder="비밀번호를 입력해주세요." v-model.trim="password" />

        <label for="passwordConfirm">비밀번호 확인</label>
        <input id="passwordConfirm" type="password" placeholder="비밀번호를 한 번 더 입력해주세요." v-model.trim="passwordConfirm" />

        <label for="profileImage">프로필 이미지</label>
        <div class="image-upload-container">
          <!-- 🔽 미리보기 영역: 기본 + or 선택 이미지 -->
          <div class="image-preview">
            <img
              :src="previewUrl || defaultImage"
              alt="미리보기"
              class="preview-img"
            />
          </div>

          <!-- 🔽 파일 선택 버튼 -->
          <input
            id="profileImage"
            type="file"
            accept="image/*"
            @change="handleImageUpload"
          />
        </div>


        <button type="submit" class="signup-button">회원가입</button>
      </form>

      <div class="login-link">
        이미 계정이 있으신가요?
        <a href="#">로그인하기</a>
      </div>

      <div class="divider">
        <span></span>
        <p>OR</p>
        <span></span>
      </div>
    </div>
    <div class="social-signup">
      <p>간편 회원가입하기</p>
      <div class="social-buttons">
        <a href="#"><img src="@/assets/icon/google.png" /></a>
        <a href="#"><img src="@/assets/icon/kakao.png" /></a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useUserStore } from "@/stores/user";
import defaultImg from "@/assets/icon/plus.png"; 

const store = useUserStore();

const props = defineProps({
  userType: {
    type: String,
    required: true,
  },
});

const name = ref("");
const id = ref("");
const password = ref("");
const passwordConfirm = ref("");

const profileImage = ref(null);
const previewUrl = ref("");

const defaultImage = defaultImg;


const handleImageUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    profileImage.value = file;
    previewUrl.value = URL.createObjectURL(file);
  }
};

const signup = async () => {
  if (!name.value || !id.value || !password.value || !passwordConfirm.value) {
    alert("모든 항목을 입력해주세요");
    return;
  }

  if (password.value !== passwordConfirm.value) {
    alert("비밀번호가 일치하지 않습니다.");
    return;
  }

  console.log("signup viewww");
  console.log(name.value);
  console.log(id.value);
  console.log(password.value);
  console.log(props.userType);
  console.log(profileImage.value)

  // const formData = new FormData();
  // formData.append("name", name.value);
  // formData.append("id", id.value);
  // formData.append("password", password.value);
  // formData.append("role", props.userType);

  // if (file.value) {
  //   formData.append("profileImage", profileImage.value);
  // }

  // await store.signup(formData)

  store.signup({
    name: name.value,
    id: id.value,
    password: password.value,
    role: props.userType,
    profileImage: profileImage.value,
  });
};
</script>

<style lang="scss" scoped>
.signup-container {
  margin: 0 auto;

  text-align: center;
  background-color: #fdeeee;
  font-family: "Noto Sans KR", sans-serif;

  @media (min-width: 1024px) {
    padding: 8rem 20rem;
  }

  // 태블릿
  @media (min-width: 768px) and (max-width: 1023px) {
    padding: 6rem 10rem;
  }

  // 모바일
  @media (max-width: 767px) {
    padding: 4rem 1.5rem;
  }
  h3 {
    font-size: 1.8rem;
    margin-bottom: 1.5rem;
    font-weight: bold;
  }
  .signup-box {
    margin: auto;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  .signup-form {
    display: flex;
    flex-direction: column;
    text-align: left;
    width: 80%;
    label {
      font-size: 0.9rem;
      margin: 0.8rem 0 0.2rem;
    }

    input {
      padding: 0.8rem;
      border: 0.5px solid #ccc;
      border-radius: 10px;
      margin-bottom: 0.5rem;
      outline: none;

      &:focus {
        border-color: #de9c9c;
      }
    }

    .signup-button {
      background-color: #de9c9c;
      border: none;
      color: white;
      padding: 0.8rem;
      border-radius: 6px;
      font-weight: bold;
      cursor: pointer;
      margin-top: 0.5rem;

      &:hover {
        background-color: #d88787;
      }
    }
  }

  .login-link {
    font-size: 0.9rem;
    margin-top: 1rem;

    a {
      color: #e28e8e;
      font-weight: bold;
    }
  }

  .divider {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
    margin: 2rem 0;
    color: #aaa;
    width: 80%;

    span {
      flex: 1;
      height: 1px;
      background: #ddd;
    }

    p {
      margin: 0 1rem;
    }
  }

  .social-signup {
    p {
      margin-bottom: 0.8rem;
    }

    .social-buttons {
      display: flex;
      justify-content: center;
      gap: 1rem;

      a img {
        width: 40px;
        height: 40px;
        border-radius: 50%;
      }
    }
  }
}
.image-upload-container {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin: 0.5rem 0;

  // input[type="file"] {
  //   flex-shrink: 0;
  // }
}

.image-preview {
  width: 80px;
  // height: 100px;
  border: 1px dashed #ccc;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
}

.preview-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}


</style>
