from pathlib import Path

from scripts.erp_competition_runner import (
    StudentAccount,
    ad_products_for_strategy,
    build_loan_plan,
    cleanup_deleted,
    cleanup_diagnosis,
    development_targets_for_strategy,
    generate_students,
    material_card_index_for_name,
    material_plan_for_strategy,
    navigation_retry_delays,
    needs_annual_planning,
    strategy_notes,
    write_import_csv,
    write_user_csv,
)


def test_generate_students_uses_unique_run_id():
    students = generate_students("T202605300101")

    assert [student.user_id for student in students] == [
        "测甲260530010101",
        "测乙260530010102",
        "测丙260530010103",
    ]
    assert [student.sid for student in students] == [
        "260530010101",
        "260530010102",
        "260530010103",
    ]


def test_write_import_csv_matches_teacher_template(tmp_path: Path):
    students = [
        StudentAccount("测试甲RUN", "测试甲", "RUN01"),
        StudentAccount("测试乙RUN", "测试乙", "RUN02"),
    ]

    csv_path = write_import_csv(tmp_path, students)
    content = csv_path.read_text(encoding="gbk")

    assert content.splitlines() == [
        "用户ID（必填）,姓名,学号,班级,专业,学院",
        "测试甲RUN,测试甲,RUN01,自动化测试班,信息管理,自动化测试学院",
        "测试乙RUN,测试乙,RUN02,自动化测试班,信息管理,自动化测试学院",
    ]


def test_write_user_csv_matches_user_import_template(tmp_path: Path):
    students = [
        StudentAccount("测试甲RUN", "测试甲", "RUN01"),
        StudentAccount("测试乙RUN", "测试乙", "RUN02"),
    ]

    csv_path = write_user_csv(tmp_path, students)
    content = csv_path.read_text(encoding="gbk")

    assert content.splitlines() == [
        "姓名（必填）,学号（必填）,班级,专业,学院",
        "测试甲,RUN01,自动化测试班,信息管理,自动化测试学院",
        "测试乙,RUN02,自动化测试班,信息管理,自动化测试学院",
    ]


def test_build_loan_plan_prefers_limited_loans_before_high_interest():
    loan_info = [
        {"loanTypeName": "短期贷款", "maxOfLoan": 20, "permitPeriod": 4},
        {
            "loanTypeName": "长期贷款",
            "maxOfLoan": 100,
            "permitYear": 3,
            "beginYear": 1,
            "currentYear": 2,
            "totalYear": 6,
        },
        {"loanTypeName": "高利贷", "permitPeriod": 4, "maxOfLoan": None},
    ]

    assert build_loan_plan({"cash": 20}, loan_info) == {
        "type": "短期贷款",
        "money": 20,
        "term": 4,
    }


def test_build_loan_plan_uses_long_term_when_short_term_unavailable():
    loan_info = [
        {"loanTypeName": "短期贷款", "maxOfLoan": 0, "permitPeriod": 4},
        {
            "loanTypeName": "长期贷款",
            "maxOfLoan": 80,
            "permitYear": 3,
            "beginYear": 1,
            "currentYear": 2,
            "totalYear": 6,
        },
        {"loanTypeName": "高利贷", "permitPeriod": 4, "maxOfLoan": None},
    ]

    assert build_loan_plan({"cash": 20}, loan_info) == {
        "type": "长期贷款",
        "money": 25,
        "term": 3,
    }


def test_build_loan_plan_uses_only_needed_high_interest_amount():
    loan_info = [
        {"loanTypeName": "短期贷款", "maxOfLoan": -3, "permitPeriod": 4},
        {
            "loanTypeName": "长期贷款",
            "maxOfLoan": -10,
            "permitYear": 3,
            "beginYear": 0,
            "currentYear": 3,
            "totalYear": 6,
        },
        {"loanTypeName": "高利贷", "permitPeriod": 4, "maxOfLoan": None},
    ]

    assert build_loan_plan({"cash": -10}, loan_info) == {
        "type": "高利贷",
        "money": 55,
        "term": 4,
    }


def test_build_loan_plan_skips_when_cash_is_above_floor():
    assert build_loan_plan({"cash": 35}, []) is None


def test_needs_annual_planning_only_at_first_period_before_finished():
    assert needs_annual_planning({"currentPeriod": 1}, 4)
    assert not needs_annual_planning({"currentPeriod": 1}, 5)
    assert not needs_annual_planning({"currentPeriod": 2}, 4)


def test_expanded_strategy_covers_products_markets_iso_and_materials():
    targets = development_targets_for_strategy("expanded")

    assert targets["products"] == ("P2", "P3", "P4")
    assert targets["markets"] == ("区域市场", "国内市场", "亚洲市场", "国际市场")
    assert targets["isos"] == ("PRODUCT9000质量认证", "PRODUCT1400环境认证")
    assert material_plan_for_strategy("expanded") == {"R1": 4, "R2": 4, "R3": 2, "R4": 1}
    assert ad_products_for_strategy("expanded") == ("P1", "P2", "P3", "P4")
    assert "P2/P3/P4" in "\n".join(strategy_notes("expanded"))


def test_basic_strategy_keeps_original_p1_scope():
    assert development_targets_for_strategy("basic-p1") == {"products": (), "markets": (), "isos": ()}
    assert material_plan_for_strategy("basic-p1") == {"R1": 4}
    assert ad_products_for_strategy("basic-p1") == ("P1",)


def test_material_card_index_matches_frontend_render_order():
    assert [material_card_index_for_name(name) for name in ("R1", "R2", "R3", "R4")] == [0, 1, 2, 3]
    assert material_card_index_for_name("R5") is None


def test_navigation_retry_delays_scale_with_attempts():
    assert navigation_retry_delays(1) == [0]
    assert navigation_retry_delays(4) == [0, 1.5, 3, 5]
    assert navigation_retry_delays(0) == [0]


def test_cleanup_deleted_requires_no_remaining_users():
    assert cleanup_deleted({"remaining": []})
    assert not cleanup_deleted({"remaining": [{"userId": "测甲001"}]})
    assert not cleanup_deleted(None)


def test_cleanup_diagnosis_names_game_member_reference_when_users_remain():
    diagnosis = cleanup_diagnosis({"remaining": [{"userId": "测甲001"}]})

    assert "gamegroupmember" in diagnosis
    assert "replay" in diagnosis
