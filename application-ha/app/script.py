import os

def process_file(file_path, output_file, line_number):
    with open(file_path, "r", encoding="utf-8") as f:
        lines = f.readlines()

    output_file.write(f"{os.path.basename(file_path)}\n\n")
    for line in lines:
        output_file.write(f"{line_number:04d}. {line}")
        line_number += 1
    output_file.write("\n\n")  # Отделяем файлы пустыми строками
    return line_number  # Возвращаем обновлённый номер строки


def merge_kotlin_files(directory, output_filename="merged_kotlin_files.txt"):
    line_number = 1  # Начинаем нумерацию с 1
    with open(output_filename, "w", encoding="utf-8") as output_file:
        for root, _, files in os.walk(directory):
            for file in sorted(files):  # Сортируем файлы для удобства
                if file.endswith(".kt"):
                    file_path = os.path.join(root, file)
                    line_number = process_file(file_path, output_file, line_number)

    print(f"Файл {output_filename} успешно создан.")


# Укажи путь к папке с Kotlin-файлами
directory_path = "./src"
merge_kotlin_files(directory_path)
