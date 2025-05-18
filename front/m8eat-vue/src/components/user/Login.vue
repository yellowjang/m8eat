<template>
  <div class="login-container">
    <h3>로그인</h3>

    <form class="login-form">
      <label for="email">이메일</label>
      <input id="email" type="email" placeholder="이메일을 입력해주세요." />

      <label for="password">비밀번호</label>
      <!-- <div class="password-wrapper"> -->
      <input id="password" type="password" placeholder="비밀번호를 입력해주세요." />
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
        <button class="google">G</button>
        <button class="kakao">K</button>
      </div>
    </div>
  </div>
</template>

<script setup>
// 추후 로그인 로직, 상태 관리 등 추가 가능
</script>

<style lang="scss" scoped>
.login-container {
  //   max-width: 400px;
  margin: 0 auto;
  padding: 2rem;
  text-align: center;
  background-color: #fdeeee;
  border-radius: 8px;
  font-family: "Noto Sans KR", sans-serif;

  h3 {
    font-size: 1.8rem;
    margin-bottom: 1.5rem;
    font-weight: bold;
  }

  .login-form {
    display: flex;
    flex-direction: column;
    text-align: left;

    label {
      font-size: 0.9rem;
      margin: 0.8rem 0 0.2rem;
    }

    input {
      padding: 0.8rem;
      border: 1px solid #ccc;
      border-radius: 6px;
      margin-bottom: 0.5rem;
    }

    .password-wrapper {
      position: relative;

      input {
        width: 100%;
        // padding-right: 2.5rem; // 👈 아이콘 공간 확보
      }

      //   .toggle-eye {
      //     position: absolute;
      //     right: 0.8rem;
      //     top: 50%;
      //     transform: translateY(-50%);
      //     cursor: pointer;
      //   }
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
    align-items: center;
    justify-content: center;
    margin: 2rem 0;
    color: #aaa;

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
