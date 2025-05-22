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
  const signup = (user) => {
    console.log("userStore signuppppppppppp");
    // console.log("userStore", name, id, password);
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
        router.push({ name: "mainpage" });
      })
      .catch((err) => {
        console.log(err);
      });
  };

  return { signup };
});
