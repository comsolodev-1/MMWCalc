package com.solodev.mmwcalc.data.content

data class TopicLearnContent(
    val topicId: String,
    val whatIsIt: String,
    val analogy: String,
    val formulas: List<FormulaEntry>,
    val workedExample: WorkedExample,
    val tipsAndTricks: List<String>,
    val didYouKnow: String
)

data class FormulaEntry(
    val label: String,
    val formula: String
)

data class WorkedExample(
    val problem: String,
    val solution: List<String>,
    val answer: String
)

object TopicContentRegistry {

    val ALL: List<TopicLearnContent> = listOf(

        // ── FIBONACCI ─────────────────────────────────────────────────────────
        TopicLearnContent(
            topicId  = "fibonacci",
            whatIsIt = "The Fibonacci sequence is a series of numbers where each term is the sum of the two terms before it. Starting from F(1)=1 and F(2)=1, the sequence goes: 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, ...",
            analogy  = "Imagine a pair of rabbits. Each month, every adult pair produces a new pair. The number of pairs each month follows the Fibonacci sequence — nature's own counting system.",
            formulas = listOf(
                FormulaEntry("Recurrence relation", "F(n) = F(n−1) + F(n−2)"),
                FormulaEntry("Standard seeds", "F(1) = 1,  F(2) = 1"),
                FormulaEntry("Alternate seeds", "F(1) = 0,  F(2) = 1"),
                FormulaEntry("Backward rule", "F(n−2) = F(n) − F(n−1)")
            ),
            workedExample = WorkedExample(
                problem  = "Find the 8th term of the Fibonacci sequence with standard seeds F(1)=1, F(2)=1.",
                solution = listOf(
                    "F(1) = 1",
                    "F(2) = 1",
                    "F(3) = 1 + 1 = 2",
                    "F(4) = 1 + 2 = 3",
                    "F(5) = 2 + 3 = 5",
                    "F(6) = 3 + 5 = 8",
                    "F(7) = 5 + 8 = 13",
                    "F(8) = 8 + 13 = 21"
                ),
                answer = "F(8) = 21"
            ),
            tipsAndTricks = listOf(
                "Always write out the sequence step by step — don't try to jump to the answer.",
                "To go backward, use F(n−2) = F(n) − F(n−1). e.g. if F(5)=5 and F(6)=8, then F(4) = 8 − 5 = 3.",
                "The ratio of consecutive Fibonacci numbers approaches the Golden Ratio φ ≈ 1.618.",
                "Custom seeds change everything — always check what F(1) and F(2) are before starting."
            ),
            didYouKnow = "Fibonacci numbers appear in flower petals, pinecone spirals, and even the arrangement of seeds in a sunflower. Most flowers have 3, 5, 8, 13, or 21 petals — all Fibonacci numbers!"
        ),

        // ── ARITHMETIC SEQUENCE ───────────────────────────────────────────────
        TopicLearnContent(
            topicId  = "arithmetic_sequence",
            whatIsIt = "An arithmetic sequence is a list of numbers where each term increases (or decreases) by the same fixed amount called the common difference (d). It is the most basic type of sequence in mathematics.",
            analogy  = "Your monthly salary increasing by ₱500 every year is an arithmetic sequence. If you earn ₱15,000 this year, you earn ₱15,500 next year, ₱16,000 the year after, and so on.",
            formulas = listOf(
                FormulaEntry("nth term", "aₙ = a₁ + (n − 1)d"),
                FormulaEntry("Common difference", "d = (aₙ − a₁) / (n − 1)"),
                FormulaEntry("Number of terms", "n = ((aₙ − a₁) / d) + 1"),
                FormulaEntry("Sum of n terms", "Sₙ = n/2 × (a₁ + aₙ)"),
                FormulaEntry("Sum (alternate)", "Sₙ = n/2 × (2a₁ + (n−1)d)")
            ),
            workedExample = WorkedExample(
                problem  = "Find the 10th term and sum of the arithmetic sequence: 3, 7, 11, 15, ...",
                solution = listOf(
                    "a₁ = 3,  d = 7 − 3 = 4,  n = 10",
                    "aₙ = a₁ + (n−1)d",
                    "a₁₀ = 3 + (10−1)(4)",
                    "a₁₀ = 3 + 36 = 39",
                    "S₁₀ = n/2 × (a₁ + aₙ)",
                    "S₁₀ = 10/2 × (3 + 39)",
                    "S₁₀ = 5 × 42 = 210"
                ),
                answer = "a₁₀ = 39,  S₁₀ = 210"
            ),
            tipsAndTricks = listOf(
                "The common difference d can be negative — the sequence will decrease.",
                "Always find d first by subtracting any term from the next: d = a₂ − a₁.",
                "If you know Sₙ and aₙ, you can find a₁ using: a₁ = (2Sₙ/n) − aₙ.",
                "For an even count of terms, the sum equals (number of pairs) × (first + last term)."
            ),
            didYouKnow = "Arithmetic sequences have been studied since ancient times. Carl Friedrich Gauss, as a young student, reportedly summed 1 to 100 instantly by noticing that pairing terms from both ends always gave 101 — giving 50 × 101 = 5050."
        ),

        // ── GEOMETRIC SEQUENCE ────────────────────────────────────────────────
        TopicLearnContent(
            topicId  = "geometric_sequence",
            whatIsIt = "A geometric sequence is a list of numbers where each term is multiplied by the same fixed value called the common ratio (r). Unlike arithmetic sequences that add, geometric sequences multiply.",
            analogy  = "A viral social media post is a geometric sequence. If one person shares it with 3 others, and each of those shares it with 3 more: 1, 3, 9, 27, 81... The common ratio is 3.",
            formulas = listOf(
                FormulaEntry("nth term", "aₙ = a₁ × r^(n−1)"),
                FormulaEntry("Common ratio", "r = (aₙ / a₁)^(1/(n−1))"),
                FormulaEntry("Sum of n terms", "Sₙ = a₁(1 − rⁿ) / (1 − r),  r ≠ 1"),
                FormulaEntry("Infinite sum", "S∞ = a₁ / (1 − r),  |r| < 1"),
                FormulaEntry("Position n", "n = log(aₙ/a₁) / log(r) + 1")
            ),
            workedExample = WorkedExample(
                problem  = "Find the 6th term of the geometric sequence: 2, 6, 18, 54, ...",
                solution = listOf(
                    "a₁ = 2,  r = 6/2 = 3,  n = 6",
                    "aₙ = a₁ × r^(n−1)",
                    "a₆ = 2 × 3^(6−1)",
                    "a₆ = 2 × 3⁵",
                    "a₆ = 2 × 243",
                    "a₆ = 486"
                ),
                answer = "a₆ = 486"
            ),
            tipsAndTricks = listOf(
                "Always verify r by dividing any term by the previous one: r = a₂/a₁ = a₃/a₂.",
                "If |r| < 1 (like 0.5), the sequence converges — terms get smaller and smaller.",
                "If r is negative, terms alternate between positive and negative.",
                "For large exponents, use logarithms to find n rather than brute force."
            ),
            didYouKnow = "Geometric sequences model compound interest, population growth, radioactive decay, and even the way musical notes are tuned. The equal temperament tuning system used in pianos is based on a geometric sequence with r = 2^(1/12)."
        ),

        // ── CENTRAL TENDENCY UNGROUPED ────────────────────────────────────────
        TopicLearnContent(
            topicId  = "central_tendency_ungrouped",
            whatIsIt = "Measures of central tendency describe the center or typical value of a dataset. The three main measures are: Mean (average), Median (middle value), and Mode (most frequent value).",
            analogy  = "Imagine 5 students' test scores: 70, 75, 80, 85, 90. The mean (80) tells you the average performance. The median (80) tells you the middle student. If most students scored 75, that's the mode.",
            formulas = listOf(
                FormulaEntry("Mean", "x̄ = Σx / n"),
                FormulaEntry("Median (odd n)", "Value at position (n+1)/2"),
                FormulaEntry("Median (even n)", "Average of values at positions n/2 and n/2+1"),
                FormulaEntry("Mode", "Value(s) with highest frequency")
            ),
            workedExample = WorkedExample(
                problem  = "Find the mean, median, and mode of: 4, 7, 2, 7, 9, 3, 7",
                solution = listOf(
                    "Sorted: 2, 3, 4, 7, 7, 7, 9",
                    "Mean: x̄ = (4+7+2+7+9+3+7) / 7 = 39 / 7 ≈ 5.57",
                    "Median: n=7 (odd), position = (7+1)/2 = 4th value = 7",
                    "Mode: 7 appears 3 times (most frequent)"
                ),
                answer = "Mean ≈ 5.57,  Median = 7,  Mode = 7"
            ),
            tipsAndTricks = listOf(
                "Always sort the data first before finding the median.",
                "There can be more than one mode (bimodal, multimodal) or no mode at all.",
                "The mean is affected by extreme values (outliers). Use median when data is skewed.",
                "Mode is the only measure that can be used for non-numerical (categorical) data."
            ),
            didYouKnow = "The word 'average' in everyday language usually means the mean, but in statistics, 'average' can refer to any measure of central tendency. Politicians and businesses often choose whichever measure makes their data look best!"
        ),

        // ── CENTRAL TENDENCY GROUPED ──────────────────────────────────────────
        TopicLearnContent(
            topicId  = "central_tendency_grouped",
            whatIsIt = "When data is organized into class intervals (frequency distribution table), we use special formulas to estimate the mean, median, and mode. These are approximations since we don't have the exact raw values.",
            analogy  = "Imagine survey results grouped into age ranges: 20-29, 30-39, 40-49. You don't know each person's exact age, but you can estimate the average using the midpoint of each group.",
            formulas = listOf(
                FormulaEntry("Grouped Mean", "x̄ = Σ(f × xₘ) / n"),
                FormulaEntry("Grouped Median", "Md = LB + [(n/2 − cf) / f] × i"),
                FormulaEntry("Grouped Mode", "Mo = LB + [d₁ / (d₁ + d₂)] × i"),
                FormulaEntry("Midpoint", "xₘ = (LB + UB) / 2"),
                FormulaEntry("Class width", "i = UB − LB")
            ),
            workedExample = WorkedExample(
                problem  = "Find the mean of scores grouped as: [60-69: f=3], [70-79: f=8], [80-89: f=12], [90-99: f=7]. n=30",
                solution = listOf(
                    "Midpoints: 64.5, 74.5, 84.5, 94.5",
                    "f×xₘ: 3×64.5=193.5, 8×74.5=596, 12×84.5=1014, 7×94.5=661.5",
                    "Σ(f×xₘ) = 193.5 + 596 + 1014 + 661.5 = 2465",
                    "x̄ = 2465 / 30 = 82.17"
                ),
                answer = "x̄ ≈ 82.17"
            ),
            tipsAndTricks = listOf(
                "LB (lower boundary) = lower class limit − 0.5. Always subtract 0.5, never use the raw limit.",
                "For median: find n/2 first, then locate the class where cumulative frequency first reaches n/2.",
                "d₁ and d₂ in mode formula: subtract the class before and after the modal class from the modal frequency.",
                "The class width i should be the same for all classes — verify this before calculating."
            ),
            didYouKnow = "Grouped data formulas give estimates, not exact values. The true mean could differ slightly from the grouped mean. This is why statisticians prefer raw data when possible."
        ),

        // ── DISPERSION UNGROUPED ──────────────────────────────────────────────
        TopicLearnContent(
            topicId  = "dispersion_ungrouped",
            whatIsIt = "Measures of dispersion describe how spread out the data is. A small dispersion means data points are close together; large dispersion means they are widely spread. Key measures: Range, Variance, Standard Deviation, and IQR.",
            analogy  = "Two classes both have a mean score of 75. Class A scores: 73, 74, 75, 76, 77. Class B scores: 50, 60, 75, 90, 95. Both have the same mean but very different spreads. Standard deviation reveals this difference.",
            formulas = listOf(
                FormulaEntry("Range", "R = Max − Min"),
                FormulaEntry("Sample Variance", "s² = Σ(x − x̄)² / (n − 1)"),
                FormulaEntry("Standard Deviation", "s = √s²"),
                FormulaEntry("IQR", "IQR = Q3 − Q1"),
                FormulaEntry("Quartile position", "Qₖ position = k(n+1)/4")
            ),
            workedExample = WorkedExample(
                problem  = "Find the variance and SD of: 4, 8, 6, 5, 3. n=5",
                solution = listOf(
                    "x̄ = (4+8+6+5+3)/5 = 26/5 = 5.2",
                    "Deviations: (4−5.2)²=1.44, (8−5.2)²=7.84, (6−5.2)²=0.64, (5−5.2)²=0.04, (3−5.2)²=4.84",
                    "Σ(x−x̄)² = 1.44+7.84+0.64+0.04+4.84 = 14.8",
                    "s² = 14.8 / (5−1) = 14.8 / 4 = 3.7",
                    "s = √3.7 ≈ 1.92"
                ),
                answer = "s² = 3.7,  s ≈ 1.92"
            ),
            tipsAndTricks = listOf(
                "Use n−1 (not n) for sample variance. This is called Bessel's correction.",
                "Standard deviation is in the same unit as the data. Variance is in squared units.",
                "IQR is resistant to outliers — it only uses the middle 50% of data.",
                "A small SD means data is consistent; a large SD means data is variable."
            ),
            didYouKnow = "Standard deviation is used everywhere — from quality control in manufacturing (ensuring products meet specifications) to finance (measuring investment risk). A stock with high SD is more volatile and risky."
        ),

        // ── DISPERSION GROUPED ────────────────────────────────────────────────
        TopicLearnContent(
            topicId  = "dispersion_grouped",
            whatIsIt = "When data is in a frequency distribution table, we estimate variance and standard deviation using class midpoints and frequencies. The formula structure is the same as ungrouped but uses f×(xₘ−x̄)² instead of (x−x̄)².",
            analogy  = "Instead of knowing each student's exact score, you know that 10 students scored in the 70-79 range. You treat all 10 as if they scored the midpoint (74.5) to estimate the spread.",
            formulas = listOf(
                FormulaEntry("Grouped Variance", "s² = Σf(xₘ − x̄)² / (n − 1)"),
                FormulaEntry("Grouped SD", "s = √s²"),
                FormulaEntry("Midpoint", "xₘ = (LB + UB) / 2")
            ),
            workedExample = WorkedExample(
                problem  = "Given mean x̄=74.5, find variance for: [60-69: f=3, xₘ=64.5], [70-79: f=7, xₘ=74.5], [80-89: f=5, xₘ=84.5]. n=15",
                solution = listOf(
                    "f(xₘ−x̄)²: 3×(64.5−74.5)²= 3×100 = 300",
                    "           7×(74.5−74.5)²= 7×0 = 0",
                    "           5×(84.5−74.5)²= 5×100 = 500",
                    "Σf(xₘ−x̄)² = 300 + 0 + 500 = 800",
                    "s² = 800 / (15−1) = 800/14 ≈ 57.14",
                    "s = √57.14 ≈ 7.56"
                ),
                answer = "s² ≈ 57.14,  s ≈ 7.56"
            ),
            tipsAndTricks = listOf(
                "Compute the grouped mean first — you need x̄ before you can find variance.",
                "Use class midpoints xₘ, not class limits, in the formula.",
                "Double-check your frequency total: Σf must equal n.",
                "The grouped SD will be slightly different from the true SD of raw data — it is an estimate."
            ),
            didYouKnow = "The concept of variance was formalized by Ronald Fisher in the early 20th century. He invented Analysis of Variance (ANOVA), a technique still widely used in medical research and psychology studies today."
        ),

        // ── RELATIVE POSITION UNGROUPED ───────────────────────────────────────
        TopicLearnContent(
            topicId  = "relative_position_ungrouped",
            whatIsIt = "Measures of relative position tell us where a specific value stands within the dataset. Quartiles divide data into 4 parts, deciles into 10 parts, percentiles into 100 parts, and z-scores measure how many standard deviations a value is from the mean.",
            analogy  = "If you scored in the 90th percentile on an exam, it means you scored higher than 90% of all test-takers. Your score's position relative to others is what matters — not the raw score alone.",
            formulas = listOf(
                FormulaEntry("Quartile Qₖ position", "k(n+1)/4"),
                FormulaEntry("Decile Dₖ position", "k(n+1)/10"),
                FormulaEntry("Percentile Pₖ position", "k(n+1)/100"),
                FormulaEntry("Z-score", "z = (x − x̄) / s"),
                FormulaEntry("Raw score from z", "x = x̄ + (z × s)")
            ),
            workedExample = WorkedExample(
                problem  = "Find Q1 of: 2, 5, 7, 8, 11, 14, 16, 19. n=8",
                solution = listOf(
                    "Sorted: 2, 5, 7, 8, 11, 14, 16, 19",
                    "Q1 position = 1×(8+1)/4 = 9/4 = 2.25",
                    "Position 2.25 → between 2nd and 3rd values",
                    "2nd value = 5,  3rd value = 7",
                    "Q1 = 5 + 0.25×(7−5) = 5 + 0.5 = 5.5"
                ),
                answer = "Q1 = 5.5"
            ),
            tipsAndTricks = listOf(
                "Always sort data in ascending order before finding any positional measure.",
                "If the position is a whole number, the quartile/percentile is that exact value.",
                "If the position is decimal, interpolate between the two surrounding values.",
                "A z-score of 0 means the value equals the mean. Positive z = above mean, negative z = below mean."
            ),
            didYouKnow = "Percentiles are used in standardized tests (like the NSAT/UPCAT), pediatric growth charts, and income distribution reports. The term 'percentile' was introduced by Sir Francis Galton in the 19th century."
        ),

        // ── RELATIVE POSITION GROUPED ─────────────────────────────────────────
        TopicLearnContent(
            topicId  = "relative_position_grouped",
            whatIsIt = "For grouped data, we use an interpolation formula to find quartiles, deciles, and percentiles. The formula locates which class interval contains the measure and interpolates within that class.",
            analogy  = "Finding the median in a frequency table is like asking: at what point does the cumulative crowd reach the halfway mark? You locate the row where it crosses n/2 and interpolate to find the exact position.",
            formulas = listOf(
                FormulaEntry("Qₖ (Quartile)", "LB + [(kn/4 − cf) / f] × i"),
                FormulaEntry("Dₖ (Decile)", "LB + [(kn/10 − cf) / f] × i"),
                FormulaEntry("Pₖ (Percentile)", "LB + [(kn/100 − cf) / f] × i"),
                FormulaEntry("Where:", "LB=lower boundary, cf=cumulative freq before class, f=class freq, i=width")
            ),
            workedExample = WorkedExample(
                problem  = "Find Q2 (median) given: [60-69: f=5, cf=5], [70-79: f=10, cf=15], [80-89: f=8, cf=23]. n=23",
                solution = listOf(
                    "Target = kn/4 = 2×23/4 = 11.5",
                    "Q2 class: cf first reaches 11.5 → [70-79] (cf=15)",
                    "LB = 69.5,  cf_before = 5,  f = 10,  i = 10",
                    "Q2 = 69.5 + [(11.5 − 5) / 10] × 10",
                    "Q2 = 69.5 + [6.5/10] × 10",
                    "Q2 = 69.5 + 6.5 = 76"
                ),
                answer = "Q2 = 76"
            ),
            tipsAndTricks = listOf(
                "LB = lower class limit − 0.5. Never use the raw limit directly.",
                "cf is the cumulative frequency of the class BEFORE your target class, not including it.",
                "The formula is the same for Q, D, and P — only the divisor (4, 10, 100) and k change.",
                "Always build the full cumulative frequency column first before applying the formula."
            ),
            didYouKnow = "The interpolation formula used here assumes data is uniformly distributed within each class. This is why results from grouped data may differ slightly from raw data calculations."
        ),

        // ── NORMAL DISTRIBUTION ───────────────────────────────────────────────
        TopicLearnContent(
            topicId  = "normal_distribution",
            whatIsIt = "The normal distribution is a bell-shaped, symmetric probability distribution. Most values cluster around the mean, with fewer values appearing farther away. It is the most important distribution in statistics.",
            analogy  = "Heights of adults, exam scores, and measurement errors all follow a normal distribution. Most people are near average height; very tall and very short people exist but are rare — forming the tails of the bell curve.",
            formulas = listOf(
                FormulaEntry("Z-score", "z = (x − μ) / σ"),
                FormulaEntry("Raw score", "x = μ + (z × σ)"),
                FormulaEntry("Mean from z", "μ = x − (z × σ)"),
                FormulaEntry("SD from z", "σ = (x − μ) / z"),
                FormulaEntry("68-95-99.7 Rule", "68% within ±1σ, 95% within ±2σ, 99.7% within ±3σ")
            ),
            workedExample = WorkedExample(
                problem  = "In an exam with μ=75 and σ=10, find the z-score of a student who scored 90. What percentage scored below 90?",
                solution = listOf(
                    "z = (x − μ) / σ",
                    "z = (90 − 75) / 10",
                    "z = 15 / 10 = 1.5",
                    "P(Z < 1.5) from z-table = 0.9332",
                    "93.32% of students scored below 90"
                ),
                answer = "z = 1.5,  P(Z < 1.5) = 0.9332 (93.32%)"
            ),
            tipsAndTricks = listOf(
                "A positive z-score means the value is above the mean; negative means below the mean.",
                "The area under the entire normal curve = 1 (or 100%).",
                "P(Z > z) = 1 − P(Z < z). Use this to find right-tail probabilities.",
                "The 68-95-99.7 rule is a quick estimate: about 68% of data falls within 1 SD of the mean."
            ),
            didYouKnow = "The normal distribution was discovered by Abraham de Moivre in 1733 as an approximation to the binomial distribution. Carl Friedrich Gauss later used it to model astronomical errors, which is why it is also called the Gaussian distribution."
        ),

        // ── LINEAR REGRESSION ─────────────────────────────────────────────────
        TopicLearnContent(
            topicId  = "linear_regression",
            whatIsIt = "Linear regression finds the best-fit straight line through a set of data points. It describes the relationship between two variables (x and y) and allows us to predict y values for given x values.",
            analogy  = "A store owner notices that on hotter days, more ice cream is sold. Linear regression finds the line that best predicts ice cream sales based on temperature — so they can prepare stock in advance.",
            formulas = listOf(
                FormulaEntry("Slope", "m = (nΣxy − ΣxΣy) / (nΣx² − (Σx)²)"),
                FormulaEntry("Intercept", "b = (Σy − mΣx) / n"),
                FormulaEntry("Regression line", "ŷ = mx + b"),
                FormulaEntry("Predict x from ŷ", "x = (ŷ − b) / m")
            ),
            workedExample = WorkedExample(
                problem  = "Find the regression line for: x=[1,2,3,4,5], y=[2,4,5,4,5]",
                solution = listOf(
                    "n=5, Σx=15, Σy=20, Σxy=66, Σx²=55",
                    "m = (5×66 − 15×20) / (5×55 − 15²)",
                    "m = (330 − 300) / (275 − 225)",
                    "m = 30 / 50 = 0.6",
                    "b = (20 − 0.6×15) / 5 = (20−9)/5 = 11/5 = 2.2",
                    "ŷ = 0.6x + 2.2"
                ),
                answer = "ŷ = 0.6x + 2.2"
            ),
            tipsAndTricks = listOf(
                "Build the complete table of x, y, xy, x², y² first — don't skip this step.",
                "The slope m tells direction: positive = y increases as x increases; negative = y decreases.",
                "The regression line always passes through the point (x̄, ȳ).",
                "Linear regression assumes a linear relationship — always plot data first to verify."
            ),
            didYouKnow = "The word 'regression' was coined by Francis Galton in 1886. He observed that children of tall parents tended to be shorter than their parents — 'regressing' toward the mean height of the population."
        ),

        // ── CORRELATION ───────────────────────────────────────────────────────
        TopicLearnContent(
            topicId  = "correlation",
            whatIsIt = "Correlation measures the strength and direction of the linear relationship between two variables. Pearson's r ranges from −1 to +1. A value close to ±1 indicates a strong relationship; close to 0 indicates a weak or no linear relationship.",
            analogy  = "Study hours and exam scores tend to have positive correlation — more study usually means better grades. Height and shoe size also correlate positively. Ice cream sales and drowning rates correlate positively too, but not because one causes the other (both increase in summer).",
            formulas = listOf(
                FormulaEntry("Pearson's r", "r = (nΣxy − ΣxΣy) / √[(nΣx²−(Σx)²)(nΣy²−(Σy)²)]"),
                FormulaEntry("Interpretation", "±0.90–1.00: Very strong,  ±0.70–0.89: Strong"),
                FormulaEntry("", "±0.50–0.69: Moderate,  ±0.30–0.49: Weak"),
                FormulaEntry("", "±0.00–0.29: Very weak / negligible")
            ),
            workedExample = WorkedExample(
                problem  = "Find r for: x=[1,2,3], y=[2,4,5]. n=3",
                solution = listOf(
                    "Σx=6, Σy=11, Σxy=25, Σx²=14, Σy²=45",
                    "Numerator: 3×25 − 6×11 = 75 − 66 = 9",
                    "Denom part 1: 3×14 − 6² = 42 − 36 = 6",
                    "Denom part 2: 3×45 − 11² = 135 − 121 = 14",
                    "r = 9 / √(6×14) = 9 / √84 = 9 / 9.165 ≈ 0.98"
                ),
                answer = "r ≈ 0.98 — Very strong positive correlation"
            ),
            tipsAndTricks = listOf(
                "Correlation does NOT imply causation. Two variables can correlate without one causing the other.",
                "r is unit-less — it doesn't matter what units x and y are in.",
                "r only measures LINEAR relationships. A perfect curve could still have r = 0.",
                "Always compute r² (coefficient of determination) too: it tells what % of variance in y is explained by x."
            ),
            didYouKnow = "Spurious correlations are famous in statistics. There is a near-perfect correlation between US per capita cheese consumption and the number of people who died by becoming tangled in bedsheets. Correlation without causation!"
        ),

        // ── MODULAR ARITHMETIC ────────────────────────────────────────────────
        TopicLearnContent(
            topicId  = "modular_arithmetic",
            whatIsIt = "Modular arithmetic is a system of arithmetic for integers where numbers 'wrap around' after reaching a certain value called the modulus. It is the mathematics of remainders.",
            analogy  = "A clock is the perfect example of modular arithmetic. After 12 hours, the clock wraps back to 1. So 13:00 is 1 PM — that's 13 mod 12 = 1. Similarly, 3 days after Wednesday is Saturday because (3+3) mod 7 = 6 → Saturday.",
            formulas = listOf(
                FormulaEntry("Basic mod", "a mod m = r  (0 ≤ r < m)"),
                FormulaEntry("Addition", "(a + b) mod m"),
                FormulaEntry("Subtraction", "(a − b) mod m"),
                FormulaEntry("Multiplication", "(a × b) mod m"),
                FormulaEntry("Division", "(a / b) mod m = (a × b⁻¹) mod m"),
                FormulaEntry("General form", "a = m × q + r")
            ),
            workedExample = WorkedExample(
                problem  = "Find 17 mod 5. Then find (17 + 8) mod 5.",
                solution = listOf(
                    "17 mod 5:",
                    "17 ÷ 5 = 3 remainder 2",
                    "17 mod 5 = 2",
                    "",
                    "(17 + 8) mod 5:",
                    "17 + 8 = 25",
                    "25 ÷ 5 = 5 remainder 0",
                    "(17 + 8) mod 5 = 0"
                ),
                answer = "17 mod 5 = 2,  (17+8) mod 5 = 0"
            ),
            tipsAndTricks = listOf(
                "The remainder r is always 0 ≤ r < m. If you get a negative result, add m until it's positive.",
                "For modular division, the inverse b⁻¹ only exists when gcd(b, m) = 1.",
                "You can reduce large numbers before multiplying: compute (a mod m) × (b mod m), then mod m again.",
                "Modular arithmetic is the foundation of all modern cryptography and computer security."
            ),
            didYouKnow = "The RSA encryption algorithm — used to secure your online banking and messaging apps — is entirely based on modular arithmetic. When you see 'https' in a URL, modular arithmetic is protecting your data."
        ),

        // ── ZELLER'S CONGRUENCE ───────────────────────────────────────────────
        TopicLearnContent(
            topicId  = "zellers_congruence",
            whatIsIt = "Zeller's Congruence is a mathematical algorithm developed by Christian Zeller in 1882 to calculate the day of the week for any date in the Gregorian or Julian calendar.",
            analogy  = "It's like a recipe that takes a date as ingredients and outputs a day of the week. Feed it December 25, 2025 and it tells you it's a Thursday — without looking at a calendar.",
            formulas = listOf(
                FormulaEntry("Formula", "h = (q + ⌊13(m+1)/5⌋ + K + ⌊K/4⌋ + ⌊J/4⌋ − 2J) mod 7"),
                FormulaEntry("Variables", "q=day, m=month (Jan=13,Feb=14 of prev year), K=year%100, J=⌊year/100⌋"),
                FormulaEntry("Day mapping", "0=Sat, 1=Sun, 2=Mon, 3=Tue, 4=Wed, 5=Thu, 6=Fri")
            ),
            workedExample = WorkedExample(
                problem  = "What day of the week is June 15, 2025?",
                solution = listOf(
                    "q=15, m=6, year=2025",
                    "K = 2025 mod 100 = 25",
                    "J = ⌊2025/100⌋ = 20",
                    "h = (15 + ⌊13×7/5⌋ + 25 + ⌊25/4⌋ + ⌊20/4⌋ − 2×20) mod 7",
                    "h = (15 + 18 + 25 + 6 + 5 − 40) mod 7",
                    "h = 29 mod 7 = 1"
                ),
                answer = "h=1 → Sunday (June 15, 2025 is a Sunday)"
            ),
            tipsAndTricks = listOf(
                "January and February are treated as months 13 and 14 of the PREVIOUS year. Don't forget to adjust the year.",
                "⌊ ⌋ means floor — always round DOWN, never up.",
                "The result h=0 is Saturday, NOT Sunday. The mapping is: 0=Sat, 1=Sun, 2=Mon...",
                "Double-check by using a known date first (like your birthday) before applying to unknown dates."
            ),
            didYouKnow = "Christian Zeller published this formula in 1882. Before computers, mathematicians like Zeller developed mental calculation tricks to determine the day of the week for any date — a skill called 'calendar calculation' that some people can do in seconds."
        ),

        // ── SIMPLE INTEREST ───────────────────────────────────────────────────
        TopicLearnContent(
            topicId  = "simple_interest",
            whatIsIt = "Simple interest is interest calculated only on the original principal amount. It does not compound — the interest earned each period is always the same. It is commonly used in short-term loans and some savings accounts.",
            analogy  = "You lend a friend ₱10,000 at 5% per year simple interest. Every year, they owe you ₱500 interest (5% of ₱10,000) — not 5% of the growing total. After 3 years, total interest = ₱1,500.",
            formulas = listOf(
                FormulaEntry("Interest", "I = P × r × t"),
                FormulaEntry("Maturity value", "F = P + I = P(1 + rt)"),
                FormulaEntry("Principal", "P = I / (rt)  or  P = F / (1+rt)"),
                FormulaEntry("Rate", "r = I / (Pt)"),
                FormulaEntry("Time", "t = I / (Pr)"),
                FormulaEntry("Note", "r must be in decimal form: 5% = 0.05")
            ),
            workedExample = WorkedExample(
                problem  = "Find the simple interest and maturity value if P=₱15,000, r=8% per year, t=2.5 years.",
                solution = listOf(
                    "I = P × r × t",
                    "I = 15,000 × 0.08 × 2.5",
                    "I = 15,000 × 0.2",
                    "I = ₱3,000",
                    "F = P + I = 15,000 + 3,000 = ₱18,000"
                ),
                answer = "I = ₱3,000,  F = ₱18,000"
            ),
            tipsAndTricks = listOf(
                "Always convert the rate to decimal before calculating: divide by 100.",
                "Time must be in years. If given in months, divide by 12. If in days, divide by 360 (ordinary) or 365 (exact).",
                "Philippine banks use ordinary interest (360 days) as standard.",
                "Simple interest is always less than compound interest for the same P, r, and t."
            ),
            didYouKnow = "Simple interest is still widely used in Philippine SSS and Pag-IBIG salary loans, short-term business loans, and some government bonds. For longer periods, banks switch to compound interest because it earns more."
        ),

        // ── COMPOUND INTEREST ─────────────────────────────────────────────────
        TopicLearnContent(
            topicId  = "compound_interest",
            whatIsIt = "Compound interest is interest calculated on both the principal AND the accumulated interest from previous periods. This means interest earns interest — causing exponential growth over time.",
            analogy  = "A snowball rolling downhill. It starts small but picks up more snow (interest) as it grows larger. The bigger it gets, the more snow it collects each roll. This is exactly how compound interest works.",
            formulas = listOf(
                FormulaEntry("Future value", "F = P(1 + j/m)^(mt)"),
                FormulaEntry("Principal", "P = F / (1 + j/m)^(mt)"),
                FormulaEntry("Time", "t = ln(F/P) / (m × ln(1 + j/m))"),
                FormulaEntry("Nominal rate", "j = m × [(F/P)^(1/mt) − 1]"),
                FormulaEntry("Effective rate", "ER = (1 + j/m)^m − 1"),
                FormulaEntry("Note", "j=nominal rate, m=periods/year, t=years")
            ),
            workedExample = WorkedExample(
                problem  = "Find F if P=₱20,000, j=6% compounded monthly, t=3 years.",
                solution = listOf(
                    "m=12 (monthly),  j=0.06",
                    "F = P(1 + j/m)^(mt)",
                    "F = 20,000 × (1 + 0.06/12)^(12×3)",
                    "F = 20,000 × (1.005)^36",
                    "F = 20,000 × 1.19668",
                    "F ≈ ₱23,933.61"
                ),
                answer = "F ≈ ₱23,933.61,  Interest = ₱3,933.61"
            ),
            tipsAndTricks = listOf(
                "Common values of m: 1=annually, 2=semi-annually, 4=quarterly, 12=monthly, 365=daily.",
                "The effective rate ER tells you the true annual rate after compounding. Always compare ER, not j.",
                "More frequent compounding = more interest earned. Monthly beats quarterly beats annually.",
                "Use logarithms to find t: t = ln(F/P) / (m × ln(1 + j/m))."
            ),
            didYouKnow = "Albert Einstein reportedly called compound interest 'the eighth wonder of the world.' Starting to save early makes an enormous difference — ₱1,000 saved at age 20 at 8% compounded annually becomes ₱21,724 by age 60, versus only ₱4,661 if saved at age 40."
        ),

        // ── STOCKS ────────────────────────────────────────────────────────────
        TopicLearnContent(
            topicId  = "stocks",
            whatIsIt = "A stock represents ownership in a company. When you buy stock, you become a shareholder and may receive dividends (share of profits) and benefit from price appreciation. Key metrics include dividend yield, EPS (earnings per share), and P/E ratio.",
            analogy  = "Buying stock is like buying a small slice of a pizza business. If the business earns ₱100,000 and there are 1,000 slices (shares), each slice earns ₱100 (EPS). If you own 10 slices, you earn ₱1,000.",
            formulas = listOf(
                FormulaEntry("Total Dividend", "Dividend = Shares × Dividend per Share"),
                FormulaEntry("Dividend Yield", "Yield = (Annual Dividend / Market Price) × 100"),
                FormulaEntry("EPS", "EPS = Net Income / Total Shares Outstanding"),
                FormulaEntry("P/E Ratio", "P/E = Market Price / EPS"),
                FormulaEntry("Total Return", "(Dividends + Capital Gain) / Purchase Price × 100")
            ),
            workedExample = WorkedExample(
                problem  = "A company has Net Income=₱500,000 and 100,000 shares. Market price=₱25. Annual dividend=₱2/share. Find EPS, P/E ratio, and dividend yield.",
                solution = listOf(
                    "EPS = 500,000 / 100,000 = ₱5 per share",
                    "P/E Ratio = 25 / 5 = 5x",
                    "Dividend Yield = (2 / 25) × 100 = 8%"
                ),
                answer = "EPS=₱5,  P/E=5x,  Dividend Yield=8%"
            ),
            tipsAndTricks = listOf(
                "A high P/E ratio means investors expect high future growth (or the stock is overpriced).",
                "Dividend yield compares dividend income to stock price — useful for income investors.",
                "EPS is more important than net income alone because it accounts for how many shares exist.",
                "Capital gain = selling price − purchase price. It can be negative (capital loss)."
            ),
            didYouKnow = "The Philippine Stock Exchange (PSE) was established in 1927. Today, Filipinos can invest as little as ₱5,000 through stock brokers or apps like COL Financial and First Metro Sec."
        ),

        // ── BONDS ─────────────────────────────────────────────────────────────
        TopicLearnContent(
            topicId  = "bonds",
            whatIsIt = "A bond is a loan made by an investor to a borrower (government or corporation). The borrower pays regular interest (coupon payments) and returns the face value at maturity. Bonds are generally safer than stocks but offer lower returns.",
            analogy  = "You lend ₱10,000 to the government. They promise to pay you ₱800 every year (8% coupon) for 5 years, then return your ₱10,000. You are the bondholder; the government is the issuer.",
            formulas = listOf(
                FormulaEntry("Coupon", "Coupon = Face Value × Coupon Rate"),
                FormulaEntry("Current Yield", "(Annual Coupon / Market Price) × 100"),
                FormulaEntry("Bond Price", "P = C/r × [1 − 1/(1+r)^n] + F/(1+r)^n"),
                FormulaEntry("Total Interest", "Total Interest = Coupon × Number of Periods")
            ),
            workedExample = WorkedExample(
                problem  = "A bond has Face Value=₱10,000, Coupon Rate=8%, maturity=5 years. Find annual coupon and total interest.",
                solution = listOf(
                    "Annual Coupon = Face Value × Coupon Rate",
                    "Coupon = 10,000 × 0.08 = ₱800 per year",
                    "Total Interest = Coupon × Periods",
                    "Total Interest = 800 × 5 = ₱4,000"
                ),
                answer = "Annual Coupon = ₱800,  Total Interest = ₱4,000"
            ),
            tipsAndTricks = listOf(
                "Bond price and yield move in opposite directions: when yield rises, price falls.",
                "A bond trading above face value is at a 'premium'; below face value is at a 'discount'.",
                "Current yield only considers annual coupon vs current price — it ignores capital gain/loss at maturity.",
                "Government bonds (like Philippine RTBs) are safer than corporate bonds but offer lower yields."
            ),
            didYouKnow = "The Philippine government regularly issues Retail Treasury Bonds (RTBs) that ordinary Filipinos can buy for as low as ₱5,000. These are among the safest investments available — backed by the full credit of the Republic of the Philippines."
        ),

        // ── MUTUAL FUNDS ──────────────────────────────────────────────────────
        TopicLearnContent(
            topicId  = "mutual_funds",
            whatIsIt = "A mutual fund pools money from many investors to invest in a diversified portfolio of stocks, bonds, or other securities. Each investor owns shares (NAVPS) proportional to their investment. Professional fund managers handle the investment decisions.",
            analogy  = "A mutual fund is like a group food order. Instead of each person ordering separately (and paying more), everyone pools their money together to order in bulk. You get a share proportional to what you contributed.",
            formulas = listOf(
                FormulaEntry("NAVPS", "NAVPS = NAV / Total Shares Outstanding"),
                FormulaEntry("Shares", "Shares = Amount Invested / NAVPS"),
                FormulaEntry("Returns", "(Current NAVPS − Purchase NAVPS) / Purchase NAVPS × 100"),
                FormulaEntry("Total Value", "Total Value = Shares × Current NAVPS")
            ),
            workedExample = WorkedExample(
                problem  = "You invest ₱50,000 in a mutual fund with NAVPS=₱25. After 2 years, NAVPS=₱32. Find shares, total value, and return.",
                solution = listOf(
                    "Shares = 50,000 / 25 = 2,000 shares",
                    "Total Value = 2,000 × 32 = ₱64,000",
                    "Returns = (32−25)/25 × 100 = 7/25 × 100 = 28%"
                ),
                answer = "2,000 shares,  Total Value=₱64,000,  Return=28%"
            ),
            tipsAndTricks = listOf(
                "NAVPS (Net Asset Value Per Share) changes daily based on the fund's portfolio performance.",
                "Higher NAVPS is not necessarily better — what matters is the percentage return.",
                "Mutual funds charge management fees (expense ratio) — check this before investing.",
                "Equity funds (stocks) have higher potential returns but higher risk than bond funds."
            ),
            didYouKnow = "The first modern mutual fund was created in the Netherlands in 1774. In the Philippines, the mutual fund industry is regulated by the Securities and Exchange Commission (SEC). Popular Philippine funds include FAMI, Sun Life, and BDO."
        ),

        // ── LOANS ─────────────────────────────────────────────────────────────
        TopicLearnContent(
            topicId  = "loans",
            whatIsIt = "A loan (amortization) is a debt repaid through regular equal payments. Each payment covers both interest and principal. Early payments are mostly interest; later payments are mostly principal. This structure is called an amortizing loan.",
            analogy  = "A home loan is like eating an elephant — you do it one bite at a time. Each monthly payment chips away at the debt. At first, most of your payment goes to interest (the bank's fee), but over time more goes to reducing your actual debt.",
            formulas = listOf(
                FormulaEntry("Monthly payment", "M = P[r(1+r)^n] / [(1+r)^n − 1]"),
                FormulaEntry("Where", "r = monthly rate = annual rate/12/100"),
                FormulaEntry("Total interest", "Total Interest = M×n − P"),
                FormulaEntry("Find n", "n = −ln(1 − Pr/M) / ln(1+r)")
            ),
            workedExample = WorkedExample(
                problem  = "Find the monthly payment for a ₱100,000 loan at 12% annual interest for 2 years (24 months).",
                solution = listOf(
                    "r = 12% / 12 / 100 = 0.01 monthly",
                    "n = 24 months",
                    "M = 100,000 × [0.01(1.01)^24] / [(1.01)^24 − 1]",
                    "M = 100,000 × [0.01 × 1.2697] / [1.2697 − 1]",
                    "M = 100,000 × 0.012697 / 0.2697",
                    "M = 100,000 × 0.04707",
                    "M ≈ ₱4,707 per month"
                ),
                answer = "M ≈ ₱4,707/month,  Total Interest = ₱12,968"
            ),
            tipsAndTricks = listOf(
                "Always convert annual rate to monthly: r = annual rate / 12 / 100.",
                "The total interest paid = (M × n) − P. This can be surprisingly large for long loans.",
                "Paying extra principal reduces the loan faster and saves significant interest.",
                "n is in months for monthly payments. A 5-year loan = 60 monthly payments."
            ),
            didYouKnow = "The word 'mortgage' comes from Old French meaning 'death pledge' — the debt dies when it is paid off. A typical Philippine home loan spans 20–30 years, meaning total interest paid can exceed the original loan amount."
        ),

        // ── CREDIT CARDS ─────────────────────────────────────────────────────
        TopicLearnContent(
            topicId  = "credit_cards",
            whatIsIt = "A credit card allows you to borrow money up to a credit limit for purchases. If you pay the full balance monthly, no interest is charged. If you carry a balance, high monthly interest rates accumulate quickly — making credit cards one of the most expensive forms of debt.",
            analogy  = "A credit card is like a short-term interest-free loan — but ONLY if you pay in full every month. Carrying a balance is like having a leak in your wallet: money slowly drains out as interest every single month.",
            formulas = listOf(
                FormulaEntry("Monthly Interest", "Interest = Balance × Monthly Rate"),
                FormulaEntry("Min. Payment", "max(Balance × min%, floor amount)"),
                FormulaEntry("BSP Cap", "Maximum monthly rate = 2% (BSP Circular 1098)"),
                FormulaEntry("New Balance", "New Balance = Old Balance + Interest − Payment")
            ),
            workedExample = WorkedExample(
                problem  = "Balance=₱20,000, monthly rate=2%, min payment=2% of balance or ₱500 floor. Find first month interest and min payment.",
                solution = listOf(
                    "Monthly Interest = 20,000 × 0.02 = ₱400",
                    "Min Payment = max(20,000×0.02, 500)",
                    "Min Payment = max(400, 500) = ₱500",
                    "Principal paid = 500 − 400 = ₱100",
                    "New Balance = 20,000 + 400 − 500 = ₱19,900"
                ),
                answer = "Interest=₱400,  Min Payment=₱500,  New Balance=₱19,900"
            ),
            tipsAndTricks = listOf(
                "Paying only the minimum payment means most of your payment goes to interest, not debt reduction.",
                "At 2% monthly, that's 24% annually — far higher than most loans or investments.",
                "Always pay more than the minimum. Even ₱100 extra per month makes a big difference.",
                "The BSP caps credit card interest at 2% per month. If your card charges more, report it."
            ),
            didYouKnow = "If you have a ₱20,000 credit card balance and only pay the minimum (2% or ₱500/month, floor), it would take over 5 years to pay off and you'd pay nearly ₱15,000 in interest — 75% more than you originally owed!"
        )
    )

    fun findById(topicId: String): TopicLearnContent? = ALL.find { it.topicId == topicId }
}