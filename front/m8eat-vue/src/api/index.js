import { useUserStore } from "@/stores/user";
import axios from "axios";

// axios.defaults.withCredentials = true;

const api = axios.create({
  baseURL: import.meta.env.VITE_M8EAT_API_URL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true
});

api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      const store = useUserStore();
      store.loginUser = null;

      alert("로그인 세션이 만료되었습니다. 다시 로그인해주세요.")
      router.push({name: 'login'})
    }
    return Promise.reject(error)
  }
)

export default api;
