<template>
  <div>
    <nav class="nav">
      <a href="/">Dashboard</a>
      <a href="/bank">Bank</a>
      <a href="/stocks">Stocks</a>
      <a href="/advisor">AI Advisor</a>
    </nav>

    <router-view />
  </div>
</template>

<style scoped>
.nav {
  display: flex;
  gap: 18px;
  padding: 14px 20px;
  border-bottom: 1px solid #eee;
}
.nav a {
  text-decoration: none;
  font-weight: 700;
  color: #333;
}
</style>
