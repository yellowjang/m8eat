import { ref, nextTick } from "vue";
import { defineStore } from "pinia";
import router from "@/router";
import api from "@/api";

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
    api
      .post(`${REST_API_URL}/auth/signup`, requestBody)
      .then((response) => {
        console.log(response.data);
        router.push({name: 'login'})
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const login = async (userInfo) => {
    try {
      await api.post(`${REST_API_URL}/auth/login`, {
          id: userInfo.id,
          password: userInfo.password,
      });

      // ✅ 로그인 성공 → 유저 정보 받아오기
      const res = await api.get(`${REST_API_URL}/auth/check`);
      
      console.log(res.data)
      loginUser.value = res.data;

      return { success: true, message: '로그인 성공' }
      
    } catch (err) {
      console.log(err);
      return {success: false, message: err.response?.data?.message || '로그인 실패'}
    }
  };

  const checkLogin = async () => {
    const res = await api.get(`${REST_API_URL}/auth/check`);
    loginUser.value = res.data; // ✅ 다시 로그인 상태로 복구
  };

  const logout = async () => {
    try {
      await api.post(`${REST_API_URL}/auth/logout`, null)
  
      loginUser.value = null;
      // ✅ DOM 반영까지 기다림
      await nextTick(); 

    } catch (err) {
        console.error("로그아웃 실패", err);
        throw err; // 필요 시 헤더에서 처리할 수 있게
    }
  }



  return { signup, login, loginUser, checkLogin, logout };
});
