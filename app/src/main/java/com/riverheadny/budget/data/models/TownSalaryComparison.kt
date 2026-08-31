package com.riverheadny.budget.data.models

/**
 * Elected and appointed salaries across five East End and Suffolk towns, from each town's own
 * adopted budget. Ported from iOS SalaryComparisonView.swift.
 *
 * Towns do not carry the same offices, so a null is a real answer — it means the office does not
 * exist there or is not separately salaried, not that the figure is unknown.
 */

data class TownSalarySnapshot(
    val town: String,
    val supervisorSalary: Double?,
    val councilSalary: Double?,
    val townClerkSalary: Double?,
    val registrarStipend: Double?,
    val townJusticeSalary: Double?,
    val taxReceiverSalary: Double?,
    val assessorChairSalary: Double?,
    val assessorSalary: Double?,
    val trusteeSalary: Double?,
    val highwaySuperSalary: Double?,
    val dataYear: Int,
    val sourceNote: String,
) {
    val isRiverhead: Boolean get() = town == "Riverhead"
}

object TownSalaryComparison {
    val snapshots: List<TownSalarySnapshot> = listOf(
        TownSalarySnapshot(
            town = "Riverhead",
            supervisorSalary = 110000.0,
            councilSalary = 50558.0,
            townClerkSalary = 96085.0,
            registrarStipend = 5000.0,
            townJusticeSalary = 96872.0,
            taxReceiverSalary = 96085.0,
            assessorChairSalary = 110663.0,
            assessorSalary = 96085.0,
            trusteeSalary = null,
            highwaySuperSalary = 107967.0,
            dataYear = 2026,
            sourceNote = "Riverhead 2026 elected/payroll snapshot: Town Clerk \$96,085; Registrar stipend \$5,000; Town Justice \$96,872; Tax Receiver \$96,085; Assessor Chair \$110,663; Assessor \$96,085; Highway Superintendent \$107,967.",
        ),
        TownSalarySnapshot(
            town = "Brookhaven",
            supervisorSalary = 177366.0,
            councilSalary = 103464.0,
            townClerkSalary = 133250.0,
            registrarStipend = null,
            townJusticeSalary = null,
            taxReceiverSalary = 122747.0,
            assessorChairSalary = null,
            assessorSalary = null,
            trusteeSalary = null,
            highwaySuperSalary = 169125.0,
            dataYear = 2026,
            sourceNote = "Brookhaven 2026 Adopted Operating Budget (Salaries of Elected Officials): Supervisor \$177,366; Council \$103,464; Tax Receiver \$122,747; Town Clerk \$133,250; Highway Superintendent \$169,125.",
        ),
        TownSalarySnapshot(
            town = "Smithtown",
            supervisorSalary = 161694.0,
            councilSalary = 95451.0,
            townClerkSalary = 91779.0,
            registrarStipend = 22500.0,
            townJusticeSalary = null,
            taxReceiverSalary = 95451.0,
            assessorChairSalary = null,
            assessorSalary = 200364.0,
            trusteeSalary = null,
            highwaySuperSalary = 155782.0,
            dataYear = 2026,
            sourceNote = "Smithtown 2026 elected/payroll snapshot: Supervisor \$161,694; Councilmember \$95,451; Assessor \$200,364; Receiver of Taxes \$95,451; Town Clerk \$91,779; Registrar stipend \$22,500; Highway Superintendent \$155,782.",
        ),
        TownSalarySnapshot(
            town = "East Hampton",
            supervisorSalary = 148350.0,
            councilSalary = 93564.0,
            townClerkSalary = 125408.0,
            registrarStipend = null,
            townJusticeSalary = null,
            taxReceiverSalary = null,
            assessorChairSalary = 119442.0,
            assessorSalary = 108073.0,
            trusteeSalary = null,
            highwaySuperSalary = 125408.0,
            dataYear = 2026,
            sourceNote = "East Hampton 2026 elected/payroll snapshot: Supervisor \$148,350; Town Clerk \$125,408; Assessor Chair \$119,442; Assessor \$108,073; Councilmember \$93,564; Highway Superintendent \$125,408.",
        ),
        TownSalarySnapshot(
            town = "Southold",
            supervisorSalary = 129502.0,
            councilSalary = 44370.0,
            townClerkSalary = 122038.0,
            registrarStipend = null,
            townJusticeSalary = 65838.0,
            taxReceiverSalary = 47616.0,
            assessorChairSalary = null,
            assessorSalary = 91216.0,
            trusteeSalary = 26234.0,
            highwaySuperSalary = 126653.0,
            dataYear = 2026,
            sourceNote = "Southold 2026 elected/payroll snapshot: Supervisor \$129,502; Council \$44,370; Town Justice \$65,838; Town Clerk \$122,038; Highway Superintendent \$126,653; Tax Receiver \$47,616; Assessor \$91,216; Trustee \$26,234.",
        ),
    )

    /** Offices in display order, with an accessor so the table can stay data-driven. */
    val offices: List<Pair<String, (TownSalarySnapshot) -> Double?>> = listOf(
        "Supervisor" to { it.supervisorSalary },
        "Council member" to { it.councilSalary },
        "Town Clerk" to { it.townClerkSalary },
        "Registrar stipend" to { it.registrarStipend },
        "Town Justice" to { it.townJusticeSalary },
        "Receiver of Taxes" to { it.taxReceiverSalary },
        "Assessor (chair)" to { it.assessorChairSalary },
        "Assessor" to { it.assessorSalary },
        "Trustee" to { it.trusteeSalary },
        "Highway Superintendent" to { it.highwaySuperSalary },
    )
}