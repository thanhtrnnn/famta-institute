# Frontend Wireframes (Low Fidelity)

## Overview
These wireframes capture the navigation shell and key screen layouts for the refreshed FAMTA Institute frontend. Each frame emphasizes primary actions for the target persona while keeping shared components (top bar, sidebar, content header) consistent.

## Legend
```
[ ]  = Button / Action Chip
---- = Section Divider
Txt  = Text block / description
Tbl  = Data table placeholder
Frm  = Form fields stack
KPI  = Metric card
Nav  = Sidebar navigation
```

## Shell & Dashboard (Default Landing)
```
+---------------------------------------------------------------+
| LOGO | FAMTA Institute                | Role ▼ | User | Exit |
+---------------------------------------------------------------+
| Nav | KPI  KPI  KPI                                           |
|     | ----                                                         |
|     | Recent Alerts (Tbl)                                         |
|     | ----                                                         |
|     | Upcoming Sessions (Tbl)                                     |
+---------------------------------------------------------------+
| Status: Ready                                                   |
+---------------------------------------------------------------+
```

## Student Management (Giáo vụ / Admin)
```
+---------------------------------------------------------------+
| ...Top Bar...                                                  |
+---------------------------------------------------------------+
| Nav | Filters: [Search] [Grade ▼] [Status ▼]                  |
|     | ----                                                     |
|     | Student Roster (Tbl with avatar, class, guardian)        |
|     | ----                                                     |
|     | Actions: [Add Student] [Import CSV] [Export]             |
+---------------------------------------------------------------+
| Status: Ready                                                   |
+---------------------------------------------------------------+
```

## Teacher Management (Admin)
```
+---------------------------------------------------------------+
| ...Top Bar...                                                  |
+---------------------------------------------------------------+
| Nav | Filters: [Search] [Subject ▼] [Availability ▼]           |
|     | ----                                                     |
|     | Teacher Directory (Tbl)                                  |
|     | ----                                                     |
|     | Actions: [Add Teacher] [Assign Class] [Export]           |
+---------------------------------------------------------------+
| Status: Ready                                                   |
+---------------------------------------------------------------+
```

## Class Scheduling (Giáo vụ)
```
+---------------------------------------------------------------+
| ...Top Bar...                                                  |
+---------------------------------------------------------------+
| Nav | Calendar Week View                                       |
|     | ----                                                     |
|     | Class Cards (Frm-style sidebar)                           |
|     | ----                                                     |
|     | Actions: [Create Class] [Assign Room]                    |
+---------------------------------------------------------------+
| Status: Ready                                                   |
+---------------------------------------------------------------+
```

## Course Catalog (Admin)
```
+---------------------------------------------------------------+
| ...Top Bar...                                                  |
+---------------------------------------------------------------+
| Nav | Filters: [Search] [Subject ▼] [Level ▼]                  |
|     | ----                                                     |
|     | Course Tiles Grid (Txt + KPIs)                           |
|     | ----                                                     |
|     | Actions: [New Course] [Archive]                          |
+---------------------------------------------------------------+
| Status: Ready                                                   |
+---------------------------------------------------------------+
```

## Score Entry (Giáo viên)
```
+---------------------------------------------------------------+
| ...Top Bar...                                                  |
+---------------------------------------------------------------+
| Nav | Class + Subject selector [Class ▼] [Subject ▼]           |
|     | ----                                                     |
|     | Score Sheet (Tbl, editable cells)                        |
|     | ----                                                     |
|     | Actions: [Save] [Submit Final] [Download Template]       |
+---------------------------------------------------------------+
| Status: Ready                                                   |
+---------------------------------------------------------------+
```

## Account & Permissions (Admin)
```
+---------------------------------------------------------------+
| ...Top Bar...                                                  |
+---------------------------------------------------------------+
| Nav | User Filter [Search] [Role ▼]                            |
|     | ----                                                     |
|     | Account Grid (Tbl)                                       |
|     | ----                                                     |
|     | Detail Drawer (Frm)                                      |
|     | ----                                                     |
|     | Actions: [Invite User] [Reset Password]                  |
+---------------------------------------------------------------+
| Status: Ready                                                   |
+---------------------------------------------------------------+
```

## Reports & Analytics (Quản lý)
```
+---------------------------------------------------------------+
| ...Top Bar...                                                  |
+---------------------------------------------------------------+
| Nav | Filters: [Timeframe ▼] [Metrics ▼]                       |
|     | ----                                                     |
|     | Charts (Line / Bar)                                      |
|     | ----                                                     |
|     | Summary KPIs (KPI cards)                                 |
|     | ----                                                     |
|     | Actions: [Export PDF] [Schedule Email]                   |
+---------------------------------------------------------------+
| Status: Ready                                                   |
+---------------------------------------------------------------+
```

## Guardian / Student Portal View (Read-Only)
```
+---------------------------------------------------------------+
| ...Top Bar...                                                  |
+---------------------------------------------------------------+
| Nav | Profile Summary (Txt + avatar)                           |
|     | ----                                                     |
|     | Grades Overview (Tbl, compact)                           |
|     | ----                                                     |
|     | Attendance Summary (Tbl)                                 |
|     | ----                                                     |
|     | Actions: [Download Report Card]                          |
+---------------------------------------------------------------+
| Status: Ready                                                   |
+---------------------------------------------------------------+
```
