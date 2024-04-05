import React, { useState } from 'react'
import styled from 'styled-components'

const FormContainer = styled.div`
  margin-top: 20px;
  margin-left: 30px;
  margin-right: 30px;
  border: 1px solid #ccc;
  padding: 10px;
`

const FormGroup = styled.div`
  margin-bottom: 10px;
`

const FormLabel = styled.label`
  display: block;
  margin-bottom: 5px;
`

const FormInput = styled.input`
  width: 100%;
  padding: 8px;
  font-size: 16px;
`

const FormButton = styled.button`
  display: block;
  width: 100%;
  padding: 10px;
  color: white;
  font-size: 16px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease-in-out;

  &:hover {
    background-color: ${props => props.theme.primaryLight};
  }
`

const EditForm = ({ coffee, onCancel }) => {
  const [formData, setFormData] = useState({
    coffeeName: coffee.coffeeName,
    unitPrice: coffee.unitPrice,
    coffeeBrewTime: coffee.coffeeBrewTime,
    coffeeImage: coffee.coffeeImage,
    coffeeDescription: coffee.coffeeDescription,
  })

  const handleChange = e => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleSubmit = e => {
    e.preventDefault()
    // 여기서 수정된 데이터를 서버로 보내어 처리하는 로직 추가
    console.log('수정된 데이터:', formData)
  }

  return (
    <FormContainer>
      <h3>메뉴 수정</h3>
      <form onSubmit={handleSubmit}>
        <FormGroup>
          <FormLabel htmlFor="coffeeName">커피 이름</FormLabel>
          <FormInput
            type="text"
            id="coffeeName"
            name="coffeeName"
            value={formData.coffeeName}
            onChange={handleChange}
          />
        </FormGroup>
        <FormGroup>
          <FormLabel htmlFor="unitPrice">단가</FormLabel>
          <FormInput
            type="number"
            id="unitPrice"
            name="unitPrice"
            value={formData.unitPrice}
            onChange={handleChange}
          />
        </FormGroup>
        <FormGroup>
          <FormLabel htmlFor="coffeeBrewTime">추출 시간</FormLabel>
          <FormInput
            type="number"
            id="coffeeBrewTime"
            name="coffeeBrewTime"
            value={formData.coffeeBrewTime}
            onChange={handleChange}
          />
        </FormGroup>
        <FormGroup>
          <FormLabel htmlFor="coffeeImage">이미지 URL</FormLabel>
          <FormInput
            type="text"
            id="coffeeImage"
            name="coffeeImage"
            value={formData.coffeeImage}
            onChange={handleChange}
          />
        </FormGroup>
        <FormGroup>
          <FormLabel htmlFor="coffeeDescription">설명</FormLabel>
          <FormInput
            type="text"
            id="coffeeDescription"
            name="coffeeDescription"
            value={formData.coffeeDescription}
            onChange={handleChange}
          />
        </FormGroup>
        <FormButton type="submit">수정 완료</FormButton>
        <FormButton type="button" onClick={onCancel}>
          취소
        </FormButton>
      </form>
    </FormContainer>
  )
}

export default EditForm
