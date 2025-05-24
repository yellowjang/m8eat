import { createRouter, createWebHistory } from "vue-router";
import MyPageView from "@/views/MyPageView.vue";
import BoardView from "@/views/BoardView.vue";
import BoardList from "@/components/board/BoardList.vue";
import BoardDetail from "@/components/board/BoardDetail.vue";
import BoardRegist from "@/components/board/BoardRegist.vue";
import Login from "@/views/LoginView.vue";
import MainView from "@/views/MainView.vue";
import LoginView from "@/views/LoginView.vue";
import SignupView from "@/views/SignupView.vue";
import DietView from "@/views/DietView.vue";
import DietView2 from "@/views/DietView2.vue";
import ApiTest from "@/components/board/ApiTest.vue";

import { useUserStore } from "@/stores/user";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "mainpage",
      component: DietView2,
    },
    //로그인 해야만 들어갈 수 있음

    {
      path: "/main/diet",
      name: "diet",
      component: DietView2,
    },
    {
      path: "/mypage",
      name: "mypage",
      component: MyPageView,
      meta: { requiresAuth: true },
    },
    {
      path: "/login",
      name: "login",
      component: LoginView,
    },
    {
      path: "/signup",
      name: "signup",
      component: SignupView,
    },
    {
      path: "/boards",
      name: "boards",
      component: BoardView,
      redirect: { path: "/" },
      meta: { requiresAuth: true },
      children: [
        {
          path: "/boards",
          name: "boardList",
          component: BoardList,
        },
        {
          path: "/boards/:boardNo",
          name: "boardDetail",
          component: BoardDetail,
        },
        {
          path: "/boards/boardForm",
          name: "boardRegist",
          component: BoardRegist,
        },
      ],
    },
    {
      path: "/apiTest",
      name: "apiTest",
      component: ApiTest,
    },
  ],
});

router.beforeEach(async (to, from, next) => {
  const store = useUserStore();

  // loginUser가 비어 있으면 로그인 복구 시도 (token 여부 상관없이)
  if (store.loginUser === null) {
    try {
      await store.checkLogin(); // 쿠키 기반으로 서버에서 판단
    } catch (e) {
      console.warn("세션 복원 실패", e);
      store.loginUser = null;
    }
  }

  const isLoggedIn = store.loginUser !== null;

  if (to.meta.requiresAuth && !isLoggedIn) {
    alert("로그인이 필요합니다.");
    next({ name: "login" });
  } else {
    next();
  }
});

// router.beforeEach(async (to, from, next) => {
//   const store = useUserStore();

//   // ✅ 로그인 상태 없고, 로그아웃 직후가 아닐 경우에만 checkLogin 시도
//   if (store.loginUser === null && !store.justLoggedOut) {
//     try {
//       await store.checkLogin();
//     } catch (e) {
//       store.loginUser = null;
//     }
//   }

//   // ✅ justLoggedOut은 한 번 쓰고 나면 초기화 (다음 라우트부턴 영향 없음)
//   store.justLoggedOut = false;

//   const isLoggedIn = store.loginUser !== null;

//   if (to.meta.requiresAuth && !isLoggedIn) {
//     alert("로그인이 필요합니다.");
//     next({ name: "login" });
//   } else {
//     next();
//   }
// });

// router.beforeEach(async (to, from, next) => {
//   const store = useUserStore();

//   // loginUser가 비어 있으면 로그인 복구 시도 (token 여부 상관없이)
//   if (store.loginUser === null) {
//     try {
//       await store.checkLogin(); // 쿠키 기반으로 서버에서 판단
//     } catch (e) {
//       console.warn("세션 복원 실패", e);
//       store.loginUser = null;
//     }
//   }

//   const isLoggedIn = store.loginUser !== null;

//   if (to.meta.requiresAuth && !isLoggedIn) {
//     alert("로그인이 필요합니다.");
//     next({ name: "login" });
//   } else {
//     next();
//   }
// });

// router.beforeEach(async (to, from, next) => {
//   const store = useUserStore();

//   const token = getTokenFromCookie(); // ✅ 직접 파싱해서 토큰 가져옴

//   // 로그인 정보가 없고, 쿠키에 accessToken이 있으면 checkLogin 시도
//   if (store.loginUser === null && token) {
//     try {
//       await store.checkLogin(); // /auth/check 요청 (withCredentials 필요)
//     } catch (error) {
//       console.warn("로그인 복구 실패", error);
//     }
//   }

//   const isLoggedIn = store.loginUser !== null;
//   console.log("🔍 라우터 가드 실행, 로그인 상태:", isLoggedIn);

//   if (to.meta.requiresAuth && !isLoggedIn) {
//     alert("로그인이 필요합니다.");
//     next({ name: "login" });
//   } else {
//     next();
//   }
// });

// function getTokenFromCookie() {
//   const cookies = document.cookie.split("; ");
//   for (const cookie of cookies) {
//     const [name, value] = cookie.split("=");
//     if (name === "accessToken") {
//       return decodeURIComponent(value); // 혹시 인코딩되어 있을 경우 대비
//     }
//   }
//   return null;
// }

// router.beforeEach(async (to, from, next) => {
//   const store = useUserStore();

//   // ✅ loginUser가 없으면 checkLogin 시도
//   if (store.loginUser === null) {
//     try {
//       await store.checkLogin(); // 쿠키 기반 로그인 복원
//     } catch {
//       // 로그인 복구 실패해도 진행
//     }
//   }

//   const isLoggedIn = store.loginUser !== null;
//   console.log("🔍 라우터 가드 실행, 로그인 상태:", isLoggedIn);

//   if (to.meta.requiresAuth && !isLoggedIn) {
//     alert("로그인이 필요합니다.");
//     next({ name: "login" });
//   } else {
//     next();
//   }
// });

export default router;
