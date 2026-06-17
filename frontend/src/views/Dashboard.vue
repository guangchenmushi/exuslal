<template>
  <div class="dashboard">
    <div class="page-header">
      <div>
        <h2>数据仪表盘</h2>
        <p class="page-desc">系统概览与数据统计</p>
      </div>
      <div class="poem-box" @click="switchPoem" title="点击切换诗句">
        <span class="poem-text" id="poemText">{{ currentPoemText }}</span>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in stats" :key="item.label">
        <el-card shadow="hover" class="stat-card" :style="{ borderTop: '3px solid ' + item.border }">
          <div class="stat-icon" :style="{ background: item.bg }">
            <el-icon :size="22"><component :is="item.icon" /></el-icon>
          </div>
          <div class="stat-body">
            <div class="stat-value" :style="{ color: item.border }">{{ item.value }}</div>
            <div class="stat-label">{{ item.label }}</div>
            <div class="stat-trend" :style="{ color: item.border }">
              <span>较上月 {{ item.trend > 0 ? '+' : '' }}{{ item.trend }}%</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第一行图表 -->
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header><span class="card-title">课程选课分布</span></template>
          <div ref="barRef" style="height:280px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header><span class="card-title">学期成绩趋势</span></template>
          <div ref="lineRef" style="height:280px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第二行图表 -->
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header><span class="card-title">用户角色分布</span></template>
          <div ref="pieRef" style="height:250px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header><span class="card-title">课程分类统计</span></template>
          <div ref="radarRef" style="height:250px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header><span class="card-title">考勤统计</span></template>
          <div ref="gaugeRef" style="height:250px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, markRaw, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { getDashboardStats } from '@/api'
import api from '@/api'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { User, Tickets, Reading, BellFilled } from '@element-plus/icons-vue'

const barRef = ref(null)
const lineRef = ref(null)
const pieRef = ref(null)
const radarRef = ref(null)
const gaugeRef = ref(null)

// 保存图表实例以便销毁
const chartInstances = []

// 安全初始化图表
function initChart(el, option) {
  if (!el) return null
  const instance = echarts.init(el)
  instance.setOption(option)
  chartInstances.push(instance)
  return instance
}

const stats = ref([
  { label: '学生人数', value: 0, trend: 5, icon: markRaw(User), border: '#0d9488', bg: 'linear-gradient(135deg,#0d9488,#14b8a6)' },
  { label: '教师人数', value: 0, trend: 2, icon: markRaw(Tickets), border: '#0891b2', bg: 'linear-gradient(135deg,#0891b2,#22d3ee)' },
  { label: '课程数量', value: 0, trend: -1, icon: markRaw(Reading), border: '#8b5cf6', bg: 'linear-gradient(135deg,#8b5cf6,#a78bfa)' },
  { label: '公告数量', value: 0, trend: 12, icon: markRaw(BellFilled), border: '#f59e0b', bg: 'linear-gradient(135deg,#f59e0b,#fbbf24)' }
])

const poems = [
  { text: '长风破浪会有时，直挂云帆济沧海。', author: '李白', title: '行路难' },
  { text: '大鹏一日同风起，扶摇直上九万里。', author: '李白', title: '上李邕' },
  { text: '天生我材必有用，千金散尽还复来。', author: '李白', title: '将进酒' },
  { text: '俱怀逸兴壮思飞，欲上青天揽明月。', author: '李白', title: '宣州谢朓楼饯别校书叔云' },
  { text: '会当凌绝顶，一览众山小。', author: '杜甫', title: '望岳' },
  { text: '黄沙百战穿金甲，不破楼兰终不还。', author: '王昌龄', title: '从军行' },
  { text: '男儿何不带吴钩，收取关山五十州。', author: '李贺', title: '南园十三首' },
  { text: '乘风好去，长空万里，直下看山河。', author: '辛弃疾', title: '太常引' },
  { text: '醉里挑灯看剑，梦回吹角连营。', author: '辛弃疾', title: '破阵子' },
  { text: '少年负壮气，奋烈自有时。', author: '李白', title: '少年行' },
  { text: '会挽雕弓如满月，西北望，射天狼。', author: '苏轼', title: '江城子·密州出猎' },
  { text: '老夫聊发少年狂，左牵黄，右擎苍。', author: '苏轼', title: '江城子·密州出猎' },
  { text: '愿将腰下剑，直为斩楼兰。', author: '李白', title: '塞下曲' },
  { text: '壮心未与年俱老，死去犹能作鬼雄。', author: '陆游', title: '书愤' },
  { text: '千磨万击还坚劲，任尔东西南北风。', author: '郑燮', title: '竹石' },
  { text: '一身报国有万死，双鬓向人无再青。', author: '陆游', title: '夜泊水村' },
  { text: '生当作人杰，死亦为鬼雄。', author: '李清照', title: '夏日绝句' },
  { text: '休言女子非英物，夜夜龙泉壁上鸣。', author: '秋瑾', title: '鹧鸪天' },
  { text: '路漫漫其修远兮，吾将上下而求索。', author: '屈原', title: '离骚' },
  { text: '大江东去，浪淘尽，千古风流人物。', author: '苏轼', title: '念奴娇·赤壁怀古' },
  { text: '男儿西北有神州，莫滴水西桥畔泪。', author: '刘克庄', title: '玉楼春' },
  { text: '报国星辰浮广岸，横戈天地展愁眉。', author: '黄遵宪', title: '塞下曲' },
  { text: '三十功名尘与土，八千里路云和月。', author: '岳飞', title: '满江红' },
  { text: '苟利国家生死以，岂因祸福避趋之。', author: '林则徐', title: '赴戍登程口占示家人' },
  { text: '我劝天公重抖擞，不拘一格降人材。', author: '龚自珍', title: '己亥杂诗' },
  { text: '拼将十万头颅血，须把乾坤力挽回。', author: '秋瑾', title: '黄海舟中日人索句并见日俄战争地图' },
  { text: '挽弓当挽强，用箭当用长。', author: '杜甫', title: '前出塞之六' },
  { text: '男儿本自重横行，天子非常赐颜色。', author: '高适', title: '燕歌行' },
  { text: '黄河落天走东海，万里写入胸怀间。', author: '李白', title: '赠裴十四' },
  { text: '新竹高于旧竹枝，全凭老干为扶持。', author: '郑燮', title: '新竹' },
  { text: '读书不觉已春深，一寸光阴一寸金。', author: '王贞白', title: '白鹿洞二首' },
  { text: '粉骨碎身浑不怕，要留清白在人间。', author: '于谦', title: '石灰吟' },
  { text: '千锤万凿出深山，烈火焚烧若等闲。', author: '于谦', title: '石灰吟' },
  { text: '臣心一片磁针石，不指南方不肯休。', author: '文天祥', title: '扬子江' },
  { text: '人生自古谁无死？留取丹心照汗青。', author: '文天祥', title: '过零丁洋' },
  { text: '但使龙城飞将在，不教胡马度阴山。', author: '王昌龄', title: '出塞' },
  { text: '三更灯火五更鸡，正是男儿读书时。', author: '颜真卿', title: '劝学' },
  { text: '操吴戈兮被犀甲，车错毂兮短兵接。', author: '屈原', title: '九歌·国殇' },
  { text: '老骥伏枥，志在千里。烈士暮年，壮心不已。', author: '曹操', title: '龟虽寿' },
  { text: '驾六龙，乘风而行。行四海，路下之八邦。', author: '曹操', title: '气出唱' },
  { text: '莫等闲，白了少年头，空悲切！', author: '岳飞', title: '满江红' },
  { text: '壮志饥餐胡虏肉，笑谈渴饮匈奴血。', author: '岳飞', title: '满江红' },
  { text: '待从头、收拾旧山河，朝天阙。', author: '岳飞', title: '满江红' },
  { text: '落红不是无情物，化作春泥更护花。', author: '龚自珍', title: '己亥杂诗' },
  { text: '雄关漫道真如铁，而今迈步从头越。', author: '毛泽东', title: '忆秦娥·娄山关' },
  { text: '红军不怕远征难，万水千山只等闲。', author: '毛泽东', title: '七律·长征' },
  { text: '为有牺牲多壮志，敢教日月换新天。', author: '毛泽东', title: '七律·到韶山' },
  { text: '数风流人物，还看今朝。', author: '毛泽东', title: '沁园春·雪' },
  { text: '自信人生二百年，会当水击三千里。', author: '毛泽东', title: '七古·残句' },
  { text: '天若有情天亦老，人间正道是沧桑。', author: '毛泽东', title: '七律·人民解放军占领南京' },
  { text: '一点浩然气，千里快哉风。', author: '苏轼', title: '水调歌头·黄州快哉亭赠张偓佺' },
  { text: '生事且弥漫，愿为持竿叟', author: '孟浩然', title: '万山潭作' },
  { text: '不要人夸好颜色，只留清气满乾坤。', author: '王冕', title: '墨梅' },
  { text: '笔落惊风雨，诗成泣鬼神。', author: '杜甫', title: '寄李十二白二十韵' },
  { text: '尔曹身与名俱灭，不废江河万古流。', author: '杜甫', title: '戏为六绝句' },
  { text: '穷且益坚，不坠青云之志。', author: '王勃', title: '滕王阁序' },
  { text: '老当益壮，宁移白首之心？', author: '王勃', title: '滕王阁序' },
  { text: '海阔凭鱼跃，天高任鸟飞。', author: '阮阅', title: '诗话总龟' },
  { text: '山当绝顶我为峰，海到无边天作岸。', author: '林则徐', title: '出老' }
]
const currentPoem = ref(poems[Math.floor(Math.random() * poems.length)])

// 格式化为带作者和标题的显示文本
const currentPoemText = ref('')
// 当 currentPoem 变化时更新显示文本
watch(currentPoem, (val) => {
  currentPoemText.value = val.text + ' ——' + val.author + '《' + val.title + '》'
}, { immediate: true })

function switchPoem() {
  let next
  do { next = poems[Math.floor(Math.random() * poems.length)] } while (next === currentPoem.value && poems.length > 1)
  currentPoem.value = next
}

onMounted(async () => {
  try {
    const res = await getDashboardStats()
    stats.value = [
      { ...stats.value[0], value: res.data.studentCount },
      { ...stats.value[1], value: res.data.teacherCount },
      { ...stats.value[2], value: res.data.courseCount },
      { ...stats.value[3], value: res.data.announcementCount }
    ]
  } catch (e) { 
    ElMessage.error(e?.response?.data?.message || e?.message || '获取仪表盘数据失败')
  }

  // 获取图表真实数据
  let chartData = null
  try {
    const r = await api.get('/dashboard/charts')
    chartData = r.data
  } catch(e) { 
    ElMessage.error(e?.response?.data?.message || e?.message || '获取图表数据失败')
  }

  nextTick(() => {
    // 1. 柱状图 - 课程选课人数
    if (barRef.value) {
      const gd = chartData?.gradeDistribution || [{name:'暂无数据',value:0}]
      const barInstance = echarts.init(barRef.value)
      chartInstances.push(barInstance)
      barInstance.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 40, right: 20, top: 20, bottom: 60 },
        xAxis: { type: 'category', data: gd.map(d => d.name), axisLine: { lineStyle: { color: '#e2e8f0' } }, axisLabel: { rotate: 30, interval: 0, fontSize: 10 } },
        yAxis: { type: 'value', splitLine: { lineStyle: { color: '#f1f5f9' } } },
        series: [{ type: 'bar', data: gd.map(d => d.value),
          itemStyle: { color: new echarts.graphic.LinearGradient(0,0,0,1, [{offset:0,color:'#0d9488'},{offset:1,color:'#5eead4'}]), borderRadius: [6,6,0,0] },
          barWidth: 36
        }]
      })
    }
    // 2. 折线图
    if (lineRef.value) {
      const gt = chartData?.gradeTrend || [{semester:'2024秋',avgScore:72},{semester:'2025春',avgScore:78},{semester:'2025秋',avgScore:74},{semester:'2026春',avgScore:82}]
      initChart(lineRef.value, {
        tooltip: { trigger: 'axis' },
        grid: { left: 40, right: 20, top: 20, bottom: 30 },
        xAxis: { type: 'category', data: gt.map(d => d.semester), axisLine: { lineStyle: { color: '#e2e8f0' } } },
        yAxis: { type: 'value', splitLine: { lineStyle: { color: '#f1f5f9' } } },
        series: [{ type: 'line', smooth: true, data: gt.map(d => d.avgScore),
          lineStyle: { color: '#0d9488', width: 3 },
          areaStyle: { color: new echarts.graphic.LinearGradient(0,0,0,1, [{offset:0,color:'rgba(13,148,136,0.25)'},{offset:1,color:'rgba(13,148,136,0)'}]) },
          symbol: 'circle', symbolSize: 8, itemStyle: { color: '#0d9488' }
        }]
      })
    }
    // 3. 饼图
    if (pieRef.value) {
      const rd = chartData?.roleDistribution || [{name:'学生',value:1},{name:'教师',value:1},{name:'管理员',value:1}]
      initChart(pieRef.value, {
        tooltip: { trigger: 'item' },
        series: [{ type: 'pie', radius: ['35%', '60%'], center: ['50%', '55%'],
          label: { show: true, formatter: '{b}\n{d}%', color: '#475569', fontSize: 11 },
          data: rd.map((d,i) => ({ ...d, itemStyle: { color: ['#0d9488','#0891b2','#f59e0b'][i] } }))
        }]
      })
    }
    // 4. 雷达图
    if (radarRef.value) {
      const cc = chartData?.courseCategories || [{name:'理工类',value:3},{name:'文科类',value:1},{name:'艺术类',value:0},{name:'体育类',value:0},{name:'医学类',value:0}]
      const maxVal = Math.max(15, Math.ceil((Math.max(...cc.map(c => c.value)) + 2) / 5) * 5)
      initChart(radarRef.value, {
        tooltip: {},
        radar: { indicator: cc.map(d => ({ name: d.name, max: maxVal })),
          shape: 'circle', splitArea: { areaStyle: { color: ['rgba(13,148,136,0.02)', 'rgba(13,148,136,0.05)'] } },
          axisLine: { lineStyle: { color: '#e2e8f0' } }
        },
        series: [{ type: 'radar',
          data: [{ value: cc.map(d => d.value), name: '课程分布' }],
          areaStyle: { color: 'rgba(13,148,136,0.15)' },
          lineStyle: { color: '#0d9488', width: 2 },
          itemStyle: { color: '#0d9488' }
        }]
      })
    }
    // 5. 仪表盘
    if (gaugeRef.value) {
      const rate = chartData?.attendanceRate || 85
      initChart(gaugeRef.value, {
        tooltip: { formatter: '{a}：{c}%' },
        series: [{ type: 'gauge', center: ['50%', '55%'], radius: '80%',
          startAngle: 210, endAngle: -30, min: 0, max: 100, splitNumber: 5,
          progress: { show: true, width: 12, itemStyle: { color: { type: 'linear', x: 0, y: 0, x2: 1, y2: 0, colorStops: [{offset:0,color:'#0d9488'},{offset:1,color:'#22c55e'}] } } },
          axisLine: { lineStyle: { width: 12, color: [[rate/100, '#e2e8f0'],[1, '#e2e8f0']] } },
          axisTick: { show: false }, splitLine: { show: false }, axisLabel: { show: false },
          detail: { fontSize: 18, fontWeight: 'bold', color: '#0f172a', offsetCenter: [0, '60%'], formatter: '{value}%' },
          data: [{ value: rate, name: '出勤率' }]
        }]
      })
    }
  })
})

// 销毁图表实例防止内存泄漏 + 窗口resize自适应
let resizeHandler
let resizeTimer
onUnmounted(() => {
  clearTimeout(resizeTimer)
  for (const instance of chartInstances) {
    instance.dispose()
  }
  chartInstances.length = 0
  if (resizeHandler) window.removeEventListener('resize', resizeHandler)
})

// 在原有的 onMounted 末尾追加 resize 监听（通过 nextTick 后添加）
// 使用 setTimeout 确保在原有 onMounted 之后执行
resizeTimer = setTimeout(() => {
  resizeHandler = () => { for (const c of chartInstances) c.resize() }
  window.addEventListener('resize', resizeHandler)
}, 100)
</script>

<style scoped>
.dashboard { max-width: 1400px; margin: 0 auto; }
.page-header { margin-bottom: 24px; display: flex; justify-content: space-between; align-items: flex-start; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #0f172a; }
.page-desc { font-size: 13px; color: #64748b; margin-top: 4px; }
.poem-box {
  background: rgba(13,148,136,0.06); padding: 8px 18px;
  border-radius: 20px; border: 1px solid rgba(13,148,136,0.12);
  cursor: pointer; user-select: none;
}
.poem-text { font-size: 14px; color: #0d9488; font-style: italic; }
.stat-card { border: none !important; border-radius: 14px !important; }
.stat-card :deep(.el-card__body) {
  display: flex; align-items: center; gap: 16px;
  padding: 20px 24px;
}
.stat-icon {
  width: 48px; height: 48px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  color: #fff; flex-shrink: 0;
}
.stat-body { flex: 1; }
.stat-value { font-size: 26px; font-weight: 700; line-height: 1.2; }
.stat-label { font-size: 13px; color: #64748b; margin-top: 2px; }
.stat-trend { font-size: 11px; margin-top: 4px; opacity: 0.7; }
.chart-card { border: none !important; border-radius: 14px !important; }
.chart-card :deep(.el-card__header) {
  border-bottom: 1px solid #f1f5f9;
  padding: 14px 20px;
}
.card-title { font-size: 14px; font-weight: 600; color: #0f172a; }
</style>
