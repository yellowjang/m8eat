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
      meta: {requiresAuth: true}
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
      meta: {requiresAuth: true},
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

  // ✅ loginUser가 없으면 checkLogin 시도
  if (store.loginUser === null) {
    try {
      await store.checkLogin(); // 쿠키 기반 로그인 복원
    } catch {
      // 로그인 복구 실패해도 진행
    }
  }

  const isLoggedIn = store.loginUser !== null;
  console.log("🔍 라우터 가드 실행, 로그인 상태:", isLoggedIn);

  if (to.meta.requiresAuth && !isLoggedIn) {
    alert("로그인이 필요합니다.");
    next({ name: "login" });
  } else {
    next();
  }
});


// router.beforeEach((to, from, next) => {
//   const store = useUserStore();
//   const isLoggedIn = store.loginUser !== null;

//   console.log("beforeEAchhhhh ", store.loginUser)

//   if (to.meta.requiresAuth && !isLoggedIn) {
//     alert("로그인이 필요합니다.")
//     next({name: 'login'})
//   } else {
//     next();
//   }
// })

export default router;
