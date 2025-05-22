<template>
  <div class="login-container">
    <div class="login-box">
      <h3>로그인</h3>

      <form class="login-form" @submit.prevent="login">
        <label for="userId">아이디</label>
        <input id="userId" type="text" placeholder="아이디를 입력해주세요." v-model.trim="id" />

        <label for="password">비밀번호</label>
        <!-- <div class="password-wrapper"> -->
        <input id="password" type="password" placeholder="비밀번호를 입력해주세요." v-model.trim="password" />
        <!-- <span class="toggle-eye">👁️</span> -->
        <!-- </div> -->

        <div class="forgot-password">
          <a href="#">비밀번호를 잊으셨나요?</a>
        </div>

        <button type="submit" class="login-button">로그인</button>
      </form>
      <div class="signup">
        아직 계정이 없으신가요?
        <a href="#">가입하기</a>
      </div>

      <div class="divider">
        <span></span>
        <p>OR</p>
        <span></span>
      </div>
      <div class="social-login">
        <p>간편 로그인하기</p>
        <div class="social-buttons">
          <a href=""><img src="@/assets/icon/google.png" /></a>
          <a href=""><img src="@/assets/icon/kakao.png" /></a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useUserStore } from "@/stores/user";

const store = useUserStore();

const id = ref("");
const password = ref("");

const login = () => {
  store.login({ id: id.value, password: password.value });
};
</script>

<style lang="scss" scoped>
.login-container {
  //   max-width: 400px;
  margin: 0 auto;
  text-align: center;
  background-color: #fdeeee;
  font-family: "Noto Sans KR", sans-serif;
  padding-top: 10rem;

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

  .login-box {
    margin: auto;
    max-width: 700px;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .login-form {
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

    .password-wrapper {
      position: relative;

      input {
        width: 100%;
        // padding-right: 2.5rem;
      }
    }

    .forgot-password {
      text-align: right;
      margin-bottom: 1rem;

      a {
        font-size: 0.85rem;
        color: #666;
        text-decoration: underline;
      }
    }

    .login-button {
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

  .signup {
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

  .social-login {
    p {
      margin-bottom: 0.8rem;
    }

    .social-buttons {
      display: flex;
      justify-content: center;
      gap: 1rem;

      button {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        border: none;
        font-size: 1.2rem;
        cursor: pointer;
      }

      .google {
        background-color: white;
        border: 1px solid #ccc;
      }

      .kakao {
        background-color: #fae100;
      }
    }
  }
}
</style>
