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
      redirect: "/main/diet",
    },
    //로그인 해야만 들어갈 수 있음

    {
      path: "/main/diet",
      name: "diet",
      component: DietView2,
      meta: { requiresAuth: true },
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

  // 로그인 여부를 명확히 판단
  const isLoggedIn = !!store.loginUser?.id;

  // 로그인 여부 불확실하면 서버에 다시 확인
  if (!isLoggedIn) {
    try {
      await store.checkLogin(); // 토큰 → 서버 확인
    } catch (e) {
      console.warn("❌ 세션 확인 실패", e);
      store.loginUser = null;
    }
  }

  const updatedLoggedIn = !!store.loginUser?.id;
  const publicPages = ["/login", "/signup", "/signup/user", "/signup/coach"];

  if (isLoggedIn && (to.name === "login" || to.name === "signup")) {
    next({ name: "diet" }); // 또는 "mainpage"
  }
  // 인증 필요한 페이지 접근 시 로그인 안 되어 있으면 → 로그인 페이지로
  if (to.meta.requiresAuth && !updatedLoggedIn) {
    alert("로그인이 필요합니다.");
    return next({ name: "login" });
  }

  // 로그인한 사용자가 로그인/회원가입으로 가려는 경우 → 홈으로 리디렉트
  if (updatedLoggedIn && publicPages.includes(to.path)) {
    return next({ name: "mainpage" });
  } else {
    next();
  }
});

export default router;
