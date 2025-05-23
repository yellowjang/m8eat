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

export default router;
