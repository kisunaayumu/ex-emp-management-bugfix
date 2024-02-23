package com.example.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.web.util.HtmlUtils;
import com.example.domain.Employee;

/**
 * employeesテーブルを操作するリポジトリ.
 * 
 * @author igamasayuki
 * 
 */
@Repository
public class EmployeeRepository {

	/**
	 * Employeeオブジェクトを生成するローマッパー.
	 */
	private static final RowMapper<Employee> EMPLOYEE_ROW_MAPPER = (rs, i) -> {
		Employee employee = new Employee();
		employee.setId(rs.getInt("id"));
		employee.setName(rs.getString("name"));
		employee.setImage(rs.getString("image"));
		employee.setGender(rs.getString("gender"));
		employee.setHireDate(rs.getDate("hire_date"));
		employee.setMailAddress(rs.getString("mail_address"));
		employee.setZipCode(rs.getString("zip_code"));
		employee.setAddress(rs.getString("address"));
		employee.setTelephone(rs.getString("telephone"));
		employee.setSalary(rs.getInt("salary"));
		employee.setCharacteristics(rs.getString("characteristics"));
		employee.setDependentsCount(rs.getInt("dependents_count"));
		return employee;
	};

	@Autowired
	private NamedParameterJdbcTemplate template;

	/**
	 * 従業員一覧情報を入社日順で取得します.
	 * 
	 * @return 全従業員一覧 従業員が存在しない場合はサイズ0件の従業員一覧を返します
	 */
	public List<Employee> findAll() {
		String sql = "SELECT id,name,image,gender,hire_date,mail_address,zip_code,address,telephone,salary,characteristics,dependents_count FROM employees ORDER BY hire_date ASC";

		List<Employee> developmentList = template.query(sql, EMPLOYEE_ROW_MAPPER);

		return developmentList;
	}

	/**
	 * 主キーから従業員情報を取得します.
	 * 
	 * @param id 検索したい従業員ID
	 * @return 検索された従業員情報
	 * @exception org.springframework.dao.DataAccessException 従業員が存在しない場合は例外を発生します
	 */
	public Employee load(Integer id) {
		String sql = "SELECT id,name,image,gender,hire_date,mail_address,zip_code,address,telephone,salary,characteristics,dependents_count FROM employees WHERE id=:id";

		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);

		Employee development = template.queryForObject(sql, param, EMPLOYEE_ROW_MAPPER);

		return development;
	}

	/**
	 * 従業員情報を変更します.
	 */
	public void update(Employee employee) {
		SqlParameterSource param = new BeanPropertySqlParameterSource(employee);

		String updateSql = "UPDATE employees SET dependents_count=:dependentsCount WHERE id=:id";
		template.update(updateSql, param);
	}

	// 曖昧検索機能追加
	public List<Employee> findByNameContaining(String name) {
    // 入力のエスケープ
    String escapedName = HtmlUtils.htmlEscape(name);

    String sql = "SELECT id,name,image,gender,hire_date,mail_address,zip_code,address,telephone,salary,characteristics,dependents_count FROM employees WHERE name LIKE :name ORDER BY hire_date ASC";

    SqlParameterSource param = new MapSqlParameterSource().addValue("name", "%" + escapedName + "%");

    List<Employee> employees = template.query(sql, param, EMPLOYEE_ROW_MAPPER);

    return employees;
	}

	public List<Employee> findWithPagination(int offset, int limit) {
		offset = Math.max(offset, 0);
		
		String sql = "SELECT id,name,image,gender,hire_date,mail_address,zip_code,address,telephone,salary,characteristics,dependents_count FROM employees ORDER BY hire_date ASC LIMIT :limit OFFSET :offset";
	
		SqlParameterSource param = new MapSqlParameterSource()
			.addValue("limit", limit)
			.addValue("offset", offset);
	
		return template.query(sql, param, EMPLOYEE_ROW_MAPPER);
	}

	public int getTotalPages(int size) {
		String countSql = "SELECT COUNT(*) FROM employees";
		int totalEmployees = template.queryForObject(countSql, new MapSqlParameterSource(), Integer.class);
		return (int) Math.ceil((double) totalEmployees / size);
	}
	public List<Employee> showList(int page, int size) {
			String sql = "SELECT * FROM Employee ORDER BY id LIMIT :limit OFFSET :offset";
			int offset = (page - 1) * size;

			SqlParameterSource param = new MapSqlParameterSource()
				.addValue("limit", size)
				.addValue("offset", offset);

			return template.query(sql, param, new BeanPropertyRowMapper<>(Employee.class));
		}

	// 曖昧検索とページング機能を組み合わせたメソッド
	public List<Employee> findByNameContainingWithPagination(String name, int offset, int limit) {
    // 入力のエスケープ
    String escapedName = HtmlUtils.htmlEscape(name);

    String sql = "SELECT id,name,image,gender,hire_date,mail_address,zip_code,address,telephone,salary,characteristics,dependents_count FROM employees WHERE name LIKE :name ORDER BY hire_date ASC LIMIT :limit OFFSET :offset";

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("name", "%" + escapedName + "%")
        .addValue("limit", limit)
        .addValue("offset", offset);

    List<Employee> employees = template.query(sql, param, EMPLOYEE_ROW_MAPPER);

    return employees;
	}

	public int getTotalPagesForName(String name, int size) {
	long totalEmployees = findByNameContaining(name).size();
	return (int) Math.ceil((double) totalEmployees / size);
	}


}
