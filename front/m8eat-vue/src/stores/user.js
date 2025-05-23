import { ref } from "vue";
import { defineStore } from "pinia";
import axios from "axios";
import router from "@/router";

const REST_API_URL = `http://localhost:8080`;

// base64url 디코딩 함수 (한글 깨짐 방지)
function base64UrlDecode(str) {
  // base64url -> base64로 변환
  str = str.replace(/-/g, "+").replace(/_/g, "/");
  // 패딩 추가
  while (str.length % 4) {
    str += "=";
  }
  // 디코딩 (한글 지원)
  try {
    return decodeURIComponent(Array.prototype.map.call(atob(str), (c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2)).join(""));
  } catch (e) {
    return atob(str);
  }
}

export const useUserStore = defineStore("user", () => {

  const loginUser = ref(null)

  const signup = (user) => {
    console.log("userStore signuppppppppppp");
    const requestBody = {
      user: {
        name: user.name,
        id: user.id,
        password: user.password,
        role: "user",
      },
      healthInfo: {
        height: 0,
        weight: 0,
        illness: "",
        allergy: "",
        purpose: "",
      },
    };
    axios
      .post(`${REST_API_URL}/auth/signup`, requestBody)
      .then((response) => {
        console.log(response.data);
        router.push({name: 'login'})
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const login = async (loginUser) => {
    try {
      const response = await axios.post(`${REST_API_URL}/auth/login`, {
          id: loginUser.id,
          password: loginUser.password,
      });
      
      const token = response.data["access-token"].split(".")
      
      const payload = JSON.parse(base64UrlDecode(token[1]))
      console.log("payload", payload)
  
      sessionStorage.setItem("access-token", response.data["access-token"])
      loginUser.value = payload
      console.log(loginUser.value)
  
      return { success: true, message: '로그인 성공' }
      
    } catch (err) {
      console.log(err);
      // alert(err.response.data.message)
      return {success: false, message: err.response.data.message}
    }
  };

  return { signup, login };
});
