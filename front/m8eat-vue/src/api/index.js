import router from "@/router";
import { useUserStore } from "@/stores/user";
import axios from "axios";

// axios.defaults.withCredentials = true;

const api = axios.create({
  baseURL: import.meta.env.VITE_M8EAT_API_URL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
    const url = error.config?.url;
    const currentPath = router.currentRoute.value.path;

    if (status === 401) {
      const store = useUserStore();

      if (url.includes("/auth/check") && currentPath !== "/login") {
        // ✅ 이미 알림을 띄운 경우에는 아무것도 하지 않음
        if (!isSessionExpiredNotified) {
          store.isSessionExpiredNotified = true;
          alert("로그인 세션이 만료되었습니다. 다시 로그인해주세요.");
          store.loginUser = null;
          router.push({ name: "login" });
        }
      }
    }

    return Promise.reject(error);
  }
);

export default api;
