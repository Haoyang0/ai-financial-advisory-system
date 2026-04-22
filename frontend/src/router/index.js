import { createRouter, createWebHistory } from "vue-router";
import LoginView from "@/views/LoginView.vue";
import RegisterView from "@/views/RegisterView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: "/", name: "home", component: () => import("../views/HomeView.vue") },
    { path: "/bank", name: "bank", component: () => import("../views/BankView.vue") },
    { path: "/stocks", name: "stocks", component: () => import("../views/StocksView.vue") },
    { path: "/advisor", name: "advisor", component: () => import("../views/AdvisorView.vue") },
    { path: "/login", name: "login", component: LoginView },
    { path: "/register", name: "register", component: RegisterView },
  ],
});

router.beforeEach((to) => {
  const publicPages = ["/login", "/register"];
  const isPublic = publicPages.includes(to.path);

  const username = localStorage.getItem("username");
  const loggedIn = !!(username && username.trim());

  // ❌ 未登录，访问受保护页面 → 去 login
  if (!isPublic && !loggedIn) {
    return "/login";
  }

  // ✅ 已登录，还访问 login / register → 回 dashboard
  if (isPublic && loggedIn) {
    return "/";
  }
});

export default router;
