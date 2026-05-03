Entry 1 - [May 2, 2026, 8:15 AM]
What I implemented: سويت فورك للمشروع وحطيت رقمي الجامعي 442050233 في الكود
Challenges encountered: كنت أتأكد من إعدادات الجافا في فيجوال ستوديو كود
How I solved it: ثبت إضافات الجافا وتأكدت أن الكود يشتغل
Testing approach: شغلت الكود الأصلي عشان أشوف المشاكل قبل التعديل
Time spent: 15 min

Entry 2 - [May 2, 2026, 8:30 AM]
What I implemented: أضفت الـ ReentrantLock عشان أحمي العدادات المشتركة وقائمة اللوق
Challenges encountered: كنت أحاول أفهم وين أنسب مكان أحط فيه اللوك
How I solved it: حطيت اللوك داخل الدوال اللي تغير العدادات عشان أمنع الـ Race Condition
Testing approach: تأكدت أن سجل العمليات يطلع كامل وبدون أخطاء
Time spent:  20 min

Entry 3 - [May 2, 2026 9:00 AM]
What I implemented: أضفت السيمفور للمعالج واستخدمت try-finally للأمان
Challenges encountered: خفت أن البرنامج يعلق لو صار خطأ واللوك ما انفتح
How I solved it: استخدمت بلوك finally عشان أضمن أن اللوك والسيمفور ينفكون دائماً
Testing approach: شغلت البرنامج أكثر من مرة والنتائج كانت ثابتة وممتازة
Time spent: 30 min

Part 2: Technical Questions
Question 1: Race Conditions
المشكلة كانت أن العدادات مثل contextSwitchCount تتغير من أكثر من ثريد بنفس الوقت وهذا يخلي الحسابات غلط وتضيع أرقام والشي الثاني الـ ArrayList حق اللوق ما يتحمل أكثر من واحد يضيف فيه بنفس اللحظة وممكن يسبب كراش للبرنامج

Question 2: Locks vs Semaphores
اللوك استعملته عشان أحمي المتغيرات والبيانات يعني واحد بس يدخل يعدل ويطلع (Mutual Exclusion) أما السيمفور استخدمته عشان أنظم دخول العمليات للمعالج وخليت له تصريح واحد بس لأن المعالج واحد

Question 3: Deadlock Prevention
الديلوك هو أن البرنامج يعلق وكل ثريد ينتظر الثاني والحل اللي سويته إني استعملت try-finally عشان أضمن أن اللوك والسيمفور ينفكون دائماً حتى لو صار خطأ وبكذا البرنامج ما يعلق أبداً

Question 4: Lock Granularity Design Decision
أنا اخترت أستعمل لوك واحد لكل العدادات (coarse-grained) لأنه أسهل وما يسبب تعقيد في الكود ومشاكل تعليق وصح أن اللوكات الكثيرة أسرع شوي بس في مشروعنا هذا لوك واحد كافي جداً ويخلي الكود أضمن

Part 5: Reflection and Learning
تعلمت أن البرمجة اللي فيها ثريدز تحتاج حذر لأن الغلط ما يبين بسرعة وعرفت كيف أستعمل اللوك والسيمفور عشان أنظم الشغل وما تضيع البيانات وأهم شي تعلمته هو حركة الفاينلي عشان البرنامج ما يعلق هذي الأشياء مهمة جداً في أنظمة الحقيقة مثل البنوك وقواعد البيانات

Part 6: GitHub Repository Information
Repository URL:https://github.com/442050233-cmd/OS-Assignment3-Starter
Number of commits: 4
Commit messages:

Update student ID to 442050233

Add ReentrantLock for counter protection

Synchronize execution log access

Final implementation with Semaphore and testing
