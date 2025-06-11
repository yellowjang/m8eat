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
import DietView2 from "@/views/DietView2.vue";
import ApiTest from "@/components/board/ApiTest.vue";
import ManageMemberView from "@/views/ManageMemberView.vue";
import { useUserStore } from "@/stores/user";
import BoardUpdate from "@/components/board/BoardUpdate.vue";
import ChatView from "@/views/ChatView.vue";

import DietDetail from "@/components/diet/DietDetail.vue";
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
      path: "/manage",
      name: "manage",
      component: ManageMemberView,
      meta: { requiresAuth: true },
    },
    {
      path: "/login",
      name: "login",
      component: LoginView,
    },
    {
      path: "/signup/:role?",
      name: "signup",
      component: SignupView,
    },
    {
      path: "/diet",
      name: "diets",
      component: DietView2,
    },
    {
      path: "/diet/:dietNo",
      name: "dietDetail",
      component: DietDetail,
    },
    // {
    //   path: "/diet/edit/:dietNo",
    //   name: "dietEdit",
    //   component: MealEditForm,
    // },
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
        {
          path: "/boards/boardUpdate/:boardNo",
          name: "boardUpdateForm",
          component: BoardUpdate,
        },
      ],
    },

    {
      path: "/apiTest",
      name: "apiTest",
      component: ApiTest,
    },
    {
      path: "/chat/:targetId",
      name: "ChatView",
      component: ChatView,
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

export default router;
