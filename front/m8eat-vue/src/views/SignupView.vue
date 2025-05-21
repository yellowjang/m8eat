<template>
  <div class="signup-container">
    <h3>회원가입</h3>
    <div class="signup-box">
      <form class="signup-form">
        <label for="name">이름</label>
        <input id="name" type="text" placeholder="이름을 입력해주세요." />

        <label for="username">아이디</label>
        <input id="username" type="text" placeholder="아이디를 입력해주세요." />

        <label for="password">비밀번호</label>
        <input id="password" type="password" placeholder="비밀번호를 입력해주세요." />

        <label for="passwordConfirm">비밀번호 확인</label>
        <input id="passwordConfirm" type="password" placeholder="비밀번호를 한 번 더 입력해주세요." />

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
// 아직 로직은 없음
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
</style>
