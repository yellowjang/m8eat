import { ref, nextTick } from "vue";
import { defineStore } from "pinia";
import router from "@/router";
import api from "@/api";
// import { isSessionExpiredNotified } from "@/api";

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
  const loginUser = ref({});
  const loginUserHealthInfo = ref({});
  const sessionExpiredNotified = ref(false);

  const signup = (user) => {
    console.log("userStore signuppppppppppp", user.profileImage);

    const formData = new FormData();

    // 🔸 1. user 객체를 JSON 문자열로 변환해서 Blob으로 추가
    formData.append(
      "user",
      new Blob([JSON.stringify({
        name: user.name,
        id: user.id,
        password: user.password,
        role: user.role,
      })], { type: "application/json" })
    );

    // 🔸 2. healthInfo 객체도 JSON으로 Blob 추가
    formData.append(
      "healthInfo",
      new Blob([JSON.stringify({
        height: null,
        weight: null,
        illness: null,
        allergy: null,
        purpose: null
      })], { type: "application/json" })
    );

    // 🔸 3. 이미지 파일 추가 (선택적으로)
    console.log("profileImage", user.profileImage)
    if (user.profileImage) {
      formData.append("profileImage", user.profileImage); // File 객체
    }

    api
      .post(`${REST_API_URL}/auth/signup`, formData, {
        headers: {
          "Content-Type": "multipart/form-data"
        }
      })
      .then((response) => {
        console.log(response.data);
        alert("회원가입 되었습니다.");
        router.push({ name: "login" });
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

      console.log(res.data);
      loginUser.value = res.data;
      console.log("qqqqqqq", loginUser.value);
      sessionExpiredNotified.value = false;
      
      return { success: true, message: "로그인 성공" };
    } catch (err) {
      console.log(err);
      return { success: false, message: err.response?.data?.message || "로그인 실패" };
    }
  };

  const getHealthInfo = async () => {
    const healthInfo = await api.get(`${REST_API_URL}/user/mypage/healthInfo`);
    loginUserHealthInfo.value  = healthInfo.data
    console.log(loginUserHealthInfo.value)
  }

  const checkLogin = async () => {
    try {
      console.log("checklogin 호출ㄹㄹㄹㄹ");
      const res = await api.get(`${REST_API_URL}/auth/check`);
      console.log("res.dataaaaaaaaaaaaa ", res.data);
      loginUser.value = res.data; // ✅ 다시 로그인 상태로 복구
      sessionExpiredNotified.value = false; // ✅ 세션 정상 복원 → 플래그 초기화
      console.log("loginUser.valueeeeeeeeee ", loginUser.value);
    } catch (error) {
      console.warn("❌ 세션 복원 실패", error);
      loginUser.value = null; // ❗ 실패 시 명시적으로 null 처리
      // 여기선 alert 안 띄워도 됨 — interceptor가 처리함
    }
  };

  const logout = async () => {
    try {
      await api.post(`${REST_API_URL}/auth/logout`, null);

      loginUser.value = null;
      // ✅ DOM 반영까지 기다림
      // await nextTick();
    } catch (err) {
      console.error("로그아웃 실패", err);
      throw err; // 필요 시 헤더에서 처리할 수 있게
    }
  };

  const updateUser = async (updateUser) => {
    console.log("store ", updateUser)
    console.log("store ", loginUser)
    try {
      await api.put(`${REST_API_URL}/user/mypage/${loginUser.userNo}`, updateUser)
    } catch (error) {
      
    }
  }

  const getCoachId = async () => {
    const response = await api.get(`${REST_API_URL}/user/mypage/coachId`);
    return response.data;
  }

  return { signup, login, loginUser, checkLogin, logout, sessionExpiredNotified, 
    updateUser, getHealthInfo, loginUserHealthInfo, getCoachId };
});
